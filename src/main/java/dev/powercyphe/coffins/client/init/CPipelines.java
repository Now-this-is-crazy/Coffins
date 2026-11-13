package dev.powercyphe.coffins.client.init;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.powercyphe.coffins.common.Coffins;
import net.minecraft.client.renderer.BindGroupLayouts;

import static net.minecraft.client.renderer.RenderPipelines.*;

public interface CPipelines {

    RenderPipeline PING_PARTICLE = register(RenderPipeline.builder(GLOBALS_SNIPPET)
            .withLocation(Coffins.id("pipeline/ping_particle"))
            .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
            .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withVertexBinding(0, DefaultVertexFormat.PARTICLE)
            .withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
            .withCull(false)
            .build());

    RenderPipeline GUI_TEXTURED_WHITE = register(RenderPipeline.builder(GUI_TEXTURED_SNIPPET)
            .withLocation(Coffins.id("pipeline/gui_textured_white"))
            .withFragmentShader(Coffins.id("core/position_tex_white"))
            .build());

    static void init() {}

}
