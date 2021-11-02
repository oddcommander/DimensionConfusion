package com.outlook.oddcommander.dic.entity;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.SilverfishEntityModel;
import net.minecraft.util.Identifier;

public class NetherSilverfishEntityRenderer extends MobEntityRenderer<NetherSilverfishEntity, SilverfishEntityModel<NetherSilverfishEntity>> {
    public NetherSilverfishEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher,new SilverfishEntityModel<>(),0.5f);
    }

    @Override
    public Identifier getTexture(NetherSilverfishEntity entity) {
        return new Identifier(DimensionConfusionCore.MODID,"textures/entity/nether_silverfish.png");
    }
}
