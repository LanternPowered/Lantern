/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInSignBook;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.util.stream.Collectors;

public class HandlerPlayInSignBook implements Handler<MessagePlayInSignBook> {

    @Override
    public void handle(NetworkContext context, MessagePlayInSignBook message) {
        final LanternPlayer player = context.getSession().getPlayer();
        final AbstractSlot slot = player.getInventory().getHotbar().getSelectedSlot();

        ItemStack itemStack = slot.peek().orElse(null);
        if (itemStack != null && itemStack.getType() == ItemTypes.WRITABLE_BOOK) {
            final ItemStack itemStack1 = new LanternItemStack(ItemTypes.WRITTEN_BOOK);
            itemStack.getValues().forEach(itemStack1::offer);
            itemStack1.offer(Keys.BOOK_PAGES, message.getPages().stream().map(Text::of).collect(Collectors.toList()));
            itemStack1.offer(Keys.BOOK_AUTHOR, Text.of(message.getAuthor()));
            itemStack1.offer(Keys.DISPLAY_NAME, Text.of(message.getTitle()));
            slot.set(itemStack1);
        }
    }
}
