package com.outlook.oddcommander.dic.mixin.accessors;

import net.minecraft.entity.SpawnRestriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SpawnRestriction.class)
public interface DicSpawnRestrictionAccessor {
    @Accessor("RESTRICTIONS")
    static Map<?,?> getRestrictions(){
        throw new AssertionError();
    }
}
