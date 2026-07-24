package com.example.auraai.presentation.home.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ShaderBrush

enum class AuraState {
    IDLE, LISTENING, PROCESSING, RESPONDING
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private const val AURA_SHADER = """
    uniform float2 resolution;
    uniform float time;
    uniform float turbulence;
    uniform float thickness;
    uniform float coreBrightness;

    mat3 rotX(float a) {
        float s = sin(a), c = cos(a);
        return mat3(1.0, 0.0, 0.0, 0.0, c, -s, 0.0, s, c);
    }
    mat3 rotY(float a) {
        float s = sin(a), c = cos(a);
        return mat3(c, 0.0, s, 0.0, 1.0, 0.0, -s, 0.0, c);
    }
    mat3 rotZ(float a) {
        float s = sin(a), c = cos(a);
        return mat3(c, -s, 0.0, s, c, 0.0, 0.0, 0.0, 1.0);
    }

    half4 main(in vec2 fragCoord) {
        // Center normalized coordinates (-0.5 to 0.5)
        vec2 uv = (fragCoord - 0.5 * resolution.xy) / min(resolution.x, resolution.y);
        
        vec3 col = vec3(0.0);
        
        // Base radius of the aura ring
        float baseRadius = 0.32 + sin(time * 0.5) * 0.012;
        
        // 7 ultra-thin 3D translucent ribbons
        const int NUM_RIBBONS = 7;
        
        for (int i = 0; i < NUM_RIBBONS; i++) {
            float fi = float(i);
            float phase = fi * 0.897; // Unique phase offset per ribbon
            
            // 3D rotation angles for orbiting and tilting over time
            float rx = sin(time * 0.25 + phase) * 0.45 + (fi - 3.0) * 0.12;
            float ry = cos(time * 0.35 + phase * 1.3) * 0.5;
            float rz = time * 0.15 + phase * 1.618;
            
            mat3 transform = rotZ(rz) * rotY(ry) * rotX(rx);
            
            // Transform 2D point into tilted 3D space
            vec3 p3d = transform * vec3(uv, 0.0);
            
            float r = length(p3d.xy);
            float theta = atan(p3d.y, p3d.x);
            float zDepth = p3d.z; // Depth: >0 is front, <0 is back
            
            // Subtle harmonic undulations along the ribbon length (maintained circular geometry)
            float wave1 = sin(theta * 3.0 + time * 1.2 + phase * 2.0);
            float wave2 = cos(theta * 5.0 - time * 1.6 + phase * 3.1);
            float wave3 = sin(theta * 2.0 + time * 0.7 + phase * 1.1);
            
            // Subdued voice deformation (calm, stable, elegant ring)
            float deform = (wave1 * 0.015 + wave2 * 0.010 + wave3 * 0.012) * (1.0 + turbulence * 0.4);
            
            float targetR = baseRadius + deform;
            
            // Distance to ribbon center line
            float dist = abs(r - targetR);
            
            // Ribbon width and soft falloff
            float ribbonWidth = (0.0025 + thickness * 0.03) * (0.8 + 0.4 * sin(theta * 2.0 + time));
            
            // Sharp core line + soft glowing aura
            float coreLine = smoothstep(ribbonWidth, 0.0, dist);
            float softGlow = 0.0012 / (dist * dist + 0.0004);
            
            float ribbonIntensity = coreLine * 1.5 + softGlow * 0.6;
            
            // Atmospheric depth fading (front is brighter, back is softer)
            float depthFactor = smoothstep(-0.3, 0.3, zDepth); // 0.0 (back) to 1.0 (front)
            float opacity = mix(0.35, 1.0, depthFactor);
            
            // Color palette: Electric Blue, Deep Indigo, Luminous Violet
            float colorPos = sin(theta * 1.5 + time * 0.4 + phase) * 0.5 + 0.5;
            
            vec3 blue = vec3(0.12, 0.45, 1.0);     // Electric Blue
            vec3 indigo = vec3(0.35, 0.20, 0.95);   // Deep Indigo
            vec3 purple = vec3(0.65, 0.15, 0.92);   // Luminous Violet
            vec3 highlight = vec3(0.55, 0.80, 1.0);  // Soft Cyan/White highlight
            
            vec3 ribbonCol = mix(blue, mix(indigo, purple, colorPos), sin(phase + theta) * 0.5 + 0.5);
            ribbonCol = mix(ribbonCol, highlight, depthFactor * 0.4 * smoothstep(0.005, 0.0, dist));
            
            // Accumulate translucent ribbon color
            float currentAlpha = ribbonIntensity * opacity * (0.4 + coreBrightness * 0.6);
            col += ribbonCol * currentAlpha;
        }
        
        // Overall soft radial bloom around ribbons (blue/purple)
        float dCenter = length(uv);
        float ringDist = abs(dCenter - baseRadius);
        float outerBloom = 0.003 / (ringDist * ringDist + 0.008) * (0.3 + turbulence * 0.5);
        vec3 bloomCol = mix(vec3(0.15, 0.3, 0.9), vec3(0.5, 0.15, 0.85), sin(time * 0.5) * 0.5 + 0.5);
        
        col += bloomCol * outerBloom;
        
        // Mask out center and outer edges so center is empty and edges are completely transparent
        float centerFade = smoothstep(0.06, 0.22, dCenter);
        float outerFade = smoothstep(0.48, 0.34, dCenter);
        float mask = centerFade * outerFade;
        
        col *= mask;
        
        // ACES tonemapping for rich vibrant colors
        col = (col * (2.51 * col + 0.03)) / (col * (2.43 * col + 0.59) + 0.14);
        
        // Calculate final alpha for premultiplied alpha compositing
        float finalAlpha = clamp(max(col.r, max(col.g, col.b)), 0.0, 1.0);
        
        return half4(min(col, 1.0), finalAlpha);
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AuraCircle(
    state: AuraState,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    // 1. Smooth low-pass filter for mic amplitude (350ms fluid transition to silence)
    val smoothedAmplitude by animateFloatAsState(
        targetValue = amplitude.coerceIn(0f, 0.8f),
        animationSpec = tween(durationMillis = 350, easing = LinearOutSlowInEasing),
        label = "smoothedAmp"
    )

    // Real-time mic factor
    val ampFactor = if (state == AuraState.LISTENING) smoothedAmplitude else 0f

    // 2. Fast 250ms base state transitions when mic becomes active
    val auraScale by animateFloatAsState(
        targetValue = when (state) {
            AuraState.IDLE -> 1.0f
            AuraState.LISTENING -> 1.07f + (ampFactor * 0.08f) // 7% base scale bump + max 8% voice scale
            AuraState.PROCESSING -> 1.04f
            AuraState.RESPONDING -> 1.02f
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "auraScale"
    )

    val baseSpeed by animateFloatAsState(
        targetValue = when (state) {
            AuraState.IDLE -> 0.30f
            AuraState.LISTENING -> 0.55f // Noticeably more fluid rotation
            AuraState.PROCESSING -> 1.50f
            AuraState.RESPONDING -> 0.45f
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "baseSpeed"
    )

    val baseTurbulence by animateFloatAsState(
        targetValue = when (state) {
            AuraState.IDLE -> 0.05f
            AuraState.LISTENING -> 0.12f // Slightly more fluid ribbon movement
            AuraState.PROCESSING -> 0.50f
            AuraState.RESPONDING -> 0.08f
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "baseTurbulence"
    )

    val baseThickness by animateFloatAsState(
        targetValue = when (state) {
            AuraState.IDLE -> 0.035f
            AuraState.LISTENING -> 0.048f // Slight bloom radius expansion
            AuraState.PROCESSING -> 0.055f
            AuraState.RESPONDING -> 0.040f
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "baseThickness"
    )

    val baseCore by animateFloatAsState(
        targetValue = when (state) {
            AuraState.IDLE -> 0.20f
            AuraState.LISTENING -> 0.38f // 20% brighter glow immediately on active
            AuraState.PROCESSING -> 0.70f
            AuraState.RESPONDING -> 0.25f
        },
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "baseCore"
    )

    // 3. Real-time mic additive effects during speech
    val currentSpeed = baseSpeed + (ampFactor * 0.40f)
    val currentTurbulence = baseTurbulence + (ampFactor * 0.20f)
    val currentThickness = baseThickness + (ampFactor * 0.035f)
    val currentCore = baseCore + (ampFactor * 0.50f)

    val speedState = rememberUpdatedState(currentSpeed)

    var time by remember { mutableFloatStateOf(0f) }

    // 4. 60 FPS phase accumulator
    LaunchedEffect(Unit) {
        var lastTime = withFrameNanos { it }
        while (true) {
            val currentTime = withFrameNanos { it }
            val deltaSec = (currentTime - lastTime) / 1_000_000_000f
            lastTime = currentTime
            time += deltaSec * speedState.value
        }
    }

    val shader = remember { RuntimeShader(AURA_SHADER) }
    val shaderBrush = remember(shader) { ShaderBrush(shader) }

    Canvas(
        modifier = modifier.scale(auraScale)
    ) {
        shader.setFloatUniform("resolution", size.width, size.height)
        shader.setFloatUniform("time", time)
        shader.setFloatUniform("turbulence", currentTurbulence)
        shader.setFloatUniform("thickness", currentThickness)
        shader.setFloatUniform("coreBrightness", currentCore)

        drawRect(brush = shaderBrush)
    }
}
