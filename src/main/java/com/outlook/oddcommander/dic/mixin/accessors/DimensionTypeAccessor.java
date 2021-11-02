package com.outlook.oddcommander.dic.mixin.accessors;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor("OVERWORLD")
    static DimensionType getOverworld(){
        throw new AssertionError();
    }

    @Accessor("THE_NETHER")
    static DimensionType getNether(){
        throw new AssertionError();
    }

    @Accessor("THE_END")
    static DimensionType getTheEnd(){
        throw new AssertionError();
    }
}
