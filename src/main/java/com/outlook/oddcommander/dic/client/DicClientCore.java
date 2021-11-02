package com.outlook.oddcommander.dic.client;

import com.outlook.oddcommander.dic.DimensionConfusionCore;
import com.outlook.oddcommander.dic.entity.NetherSilverfishEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public final class DicClientCore implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(DimensionConfusionCore.NETHER_SILVERFISH,(manager, context) -> new NetherSilverfishEntityRenderer(manager));
    }
}
