package dev.powercyphe.coffins.client.init;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.powercyphe.coffins.common.Coffins;

import static net.minecraft.client.renderer.RenderPipelines.*;

public interface CPipelines {

    RenderPipeline PING_PARTICLE = register(RenderPipeline.builder(MATRICES_PROJECTION_SNIPPET)
            .withLocation(Coffins.id("pipeline/ping_particle"))
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexFormat(DefaultVertexFormat.PARTICLE, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build());

    RenderPipeline GUI_TEXTURED_WHITE = register(RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
            .withLocation(Coffins.id("pipeline/gui_textured_white"))
            .withFragmentShader(Coffins.id("core/position_tex_white"))
            .build());

    static void init() {}

}
