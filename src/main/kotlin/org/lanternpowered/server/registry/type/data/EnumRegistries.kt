/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.server.data.type.LanternBedPart
import org.lanternpowered.server.data.type.LanternChestAttachmentType
import org.lanternpowered.server.data.type.LanternComparatorMode
import org.lanternpowered.server.data.type.LanternDoorHalf
import org.lanternpowered.server.data.type.LanternDyeColor
import org.lanternpowered.server.data.type.LanternDoorHinge
import org.lanternpowered.server.data.type.LanternInstrumentType
import org.lanternpowered.server.data.type.LanternPortionType
import org.lanternpowered.server.data.type.LanternRailDirection
import org.lanternpowered.server.data.type.LanternSlabPortion
import org.lanternpowered.server.data.type.LanternAttachmentSurface
import org.lanternpowered.server.data.type.LanternWireAttachmentType
import org.lanternpowered.server.registry.InternalCatalogTypeRegistry
import org.lanternpowered.server.registry.internalCatalogTypeRegistryOfArray
import org.spongepowered.api.data.type.AttachmentSurface
import org.spongepowered.api.data.type.ChestAttachmentType
import org.spongepowered.api.data.type.ComparatorMode
import org.spongepowered.api.data.type.DoorHinge
import org.spongepowered.api.data.type.DyeColor
import org.spongepowered.api.data.type.InstrumentType
import org.spongepowered.api.data.type.PortionType
import org.spongepowered.api.data.type.RailDirection
import org.spongepowered.api.data.type.SlabPortion
import org.spongepowered.api.data.type.WireAttachmentType

val BedPartRegistry: InternalCatalogTypeRegistry<LanternBedPart> =
        internalCatalogTypeRegistryOfArray { LanternBedPart.values() }

val ChestAttachmentTypeRegistry: InternalCatalogTypeRegistry<ChestAttachmentType> =
        internalCatalogTypeRegistryOfArray { LanternChestAttachmentType.values() }

val ComparatorTypeRegistry: InternalCatalogTypeRegistry<ComparatorMode> =
        internalCatalogTypeRegistryOfArray { LanternComparatorMode.values() }

val DoorHalfRegistry: InternalCatalogTypeRegistry<LanternDoorHalf> =
        internalCatalogTypeRegistryOfArray { LanternDoorHalf.values() }

val DyeColorRegistry: InternalCatalogTypeRegistry<DyeColor> =
        internalCatalogTypeRegistryOfArray { LanternDyeColor.values() }

val DoorHingeRegistry: InternalCatalogTypeRegistry<DoorHinge> =
        internalCatalogTypeRegistryOfArray { LanternDoorHinge.values() }

val InstrumentTypeRegistry: InternalCatalogTypeRegistry<InstrumentType> =
        internalCatalogTypeRegistryOfArray { LanternInstrumentType.values() }

val PortionTypeRegistry: InternalCatalogTypeRegistry<PortionType> =
        internalCatalogTypeRegistryOfArray { LanternPortionType.values() }

val RailDirectionRegistry: InternalCatalogTypeRegistry<RailDirection> =
        internalCatalogTypeRegistryOfArray { LanternRailDirection.values() }

val SlabPortionRegistry: InternalCatalogTypeRegistry<SlabPortion> =
        internalCatalogTypeRegistryOfArray { LanternSlabPortion.values() }

val AttachmentSurfaceRegistry: InternalCatalogTypeRegistry<AttachmentSurface> =
        internalCatalogTypeRegistryOfArray { LanternAttachmentSurface.values() }

val WireAttachmentTypeRegistry: InternalCatalogTypeRegistry<WireAttachmentType> =
        internalCatalogTypeRegistryOfArray { LanternWireAttachmentType.values() }
