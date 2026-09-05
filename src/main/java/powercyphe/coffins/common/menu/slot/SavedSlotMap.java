package powercyphe.coffins.common.menu.slot;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;

import static powercyphe.coffins.common.menu.slot.SavedSlotProvider.Type;

public class SavedSlotMap {
    public static final Codec<SavedSlotMap> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(
                            Codec.stringResolver(Object::toString, Integer::parseInt),
                            Entry.CODEC
                    ).fieldOf("map").forGetter(SavedSlotMap::get)
            ).apply(instance, SavedSlotMap::new)
    );

    private final Map<Integer, Entry> map;

    private SavedSlotMap(Map<Integer, Entry> map) {
        this.map = new HashMap<>(map);
    }

    public static SavedSlotMap create() {
        return new SavedSlotMap(new HashMap<>());
    }

    public void clear() {
        this.map.clear();
    }

    public Map<Integer, Entry> get() {
        return ImmutableMap.copyOf(this.map);
    }

    public <T extends SavedSlotProvider> void put(int slot, T provider) {
        this.map.put(slot, new Entry(provider.type(), provider));
    }

    public boolean has(int slot) {
        return this.map.containsKey(slot);
    }

    public SavedSlotProvider get(int slot) {
        return this.map.get(slot).provider();
    }

    public void remove(int slot) {
        this.map.remove(slot);
    }

    public record Entry(Type<? extends SavedSlotProvider> type, SavedSlotProvider provider) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Type.CODEC.fieldOf("type").forGetter(Entry::type),
                        SavedSlotProvider.CODEC.fieldOf("provider").forGetter(Entry::provider)
                ).apply(instance, Entry::new)
        );
    }
}
