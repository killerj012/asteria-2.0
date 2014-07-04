package server.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import server.core.net.HostGateway;
import server.core.net.packet.PacketDecoder;
import server.world.World;
import server.world.entity.npc.Npc;
import server.world.entity.npc.NpcDefinition;
import server.world.entity.npc.NpcDropTable;
import server.world.entity.npc.NpcDropTable.NpcDrop;
import server.world.entity.npc.NpcMovementCoordinator.Coordinator;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillEvent;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.item.ground.StaticGroundItem;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;
import server.world.shop.Currency;
import server.world.shop.Shop;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * A collection of miscellaneous utility methods and constants.
 * 
 * @author blakeman8192
 * @author lare96
 */
@SuppressWarnings("unused")
public final class Misc {

    /** Difference in X coordinates for directions array. */
    public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1,
            1, -1, 0, 1 };

    /** Difference in Y coordinates for directions array. */
    public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0,
            -1, -1, -1 };

    /** The login response codes. */
    public static final int LOGIN_RESPONSE_OK = 2,
            LOGIN_RESPONSE_INVALID_CREDENTIALS = 3,
            LOGIN_RESPONSE_ACCOUNT_DISABLED = 4,
            LOGIN_RESPONSE_ACCOUNT_ONLINE = 5, LOGIN_RESPONSE_UPDATED = 6,
            LOGIN_RESPONSE_WORLD_FULL = 7,
            LOGIN_RESPONSE_LOGIN_SERVER_OFFLINE = 8,
            LOGIN_RESPONSE_LOGIN_LIMIT_EXCEEDED = 9,
            LOGIN_RESPONSE_BAD_SESSION_ID = 10,
            LOGIN_RESPONSE_PLEASE_TRY_AGAIN = 11,
            LOGIN_RESPONSE_NEED_MEMBERS = 12,
            LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN = 13,
            LOGIN_RESPONSE_SERVER_BEING_UPDATED = 14,
            LOGIN_RESPONSE_LOGIN_ATTEMPTS_EXCEEDED = 16,
            LOGIN_RESPONSE_MEMBERS_ONLY_AREA = 17;

    /** The equipment slots. */
    public static final int EQUIPMENT_SLOT_HEAD = 0, EQUIPMENT_SLOT_CAPE = 1,
            EQUIPMENT_SLOT_AMULET = 2, EQUIPMENT_SLOT_WEAPON = 3,
            EQUIPMENT_SLOT_CHEST = 4, EQUIPMENT_SLOT_SHIELD = 5,
            EQUIPMENT_SLOT_LEGS = 7, EQUIPMENT_SLOT_HANDS = 9,
            EQUIPMENT_SLOT_FEET = 10, EQUIPMENT_SLOT_RING = 12,
            EQUIPMENT_SLOT_ARROWS = 13;

    /** The appearance slots. */
    public static final int APPEARANCE_SLOT_CHEST = 0,
            APPEARANCE_SLOT_ARMS = 1, APPEARANCE_SLOT_LEGS = 2,
            APPEARANCE_SLOT_HEAD = 3, APPEARANCE_SLOT_HANDS = 4,
            APPEARANCE_SLOT_FEET = 5, APPEARANCE_SLOT_BEARD = 6;

    /** The skill id's. */
    public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2,
            HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6, COOKING = 7,
            WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11,
            CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15,
            AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19,
            RUNECRAFTING = 20;

    /** The bonus id's. */
    public static final int ATTACK_STAB = 0, ATTACK_SLASH = 1,
            ATTACK_CRUSH = 2, ATTACK_MAGIC = 3, ATTACK_RANGE = 4,
            DEFENCE_STAB = 5, DEFENCE_SLASH = 6, DEFENCE_CRUSH = 7,
            DEFENCE_MAGIC = 8, DEFENCE_RANGE = 9, BONUS_STRENGTH = 10,
            BONUS_PRAYER = 11;

    /** The gender id's. */
    public static final int GENDER_MALE = 0, GENDER_FEMALE = 1;

    /** Items that are not allowed to be traded. */
    public static final int[] ITEM_UNTRADEABLE = {};

    /** Items that are not allowed to be in a shop. */
    public static final int[] NO_SHOP_ITEMS = { 995 };

    /** Items that are platebodies. */
    private static Set<Integer> isPlatebody = new HashSet<>();

    /** Items that are full helms. */
    private static Set<Integer> isFullHelm = new HashSet<>();

    /** Items that are two handed. */
    private static Set<Integer> is2H = new HashSet<>();

    /** The bonus names. */
    public static final String[] BONUS_NAMES = { "Stab", "Slash", "Crush",
            "Magic", "Range", "Stab", "Slash", "Crush", "Magic", "Range",
            "Strength", "Prayer" };

    /** The character table. */
    private static char xlateTable[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h',
            'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
            'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')',
            '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%',
            '"', '[', ']' };

    /** The decode buffer. */
    private static char decodeBuf[] = new char[4096];

    /** A table of valid characters. */
    public static final char VALID_CHARS[] = { '_', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&', '*',
            '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"', '[',
            ']', '|', '?', '/', '`' };

    /** To prevent instantiation. */
    private Misc() {
    }

    /**
     * Appends the indefinite article of a 'thing'.
     * 
     * @param thing
     *            the thing.
     * @return the indefinite article.
     */
    public static String appendIndefiniteArticle(String thing) {
        char first = thing.toLowerCase().charAt(0);
        boolean vowel = first == 'a' || first == 'e' || first == 'i'
                || first == 'o' || first == 'u';
        return vowel ? "an".concat(" " + thing) : "a".concat(" " + thing);
    }

    /**
     * Gets if the player is allowed to click an object based on the position of
     * the object and player
     * 
     * @param playerPosition
     *            the player's position.
     * @param objectPosition
     *            the object's position.
     * @param the
     *            size of the object.
     * @return true if the player is allowed to click the object.
     */
    public static boolean canClickObject(Position playerPosition,
            Position objectPosition, int size) {
        return playerPosition.withinDistance(objectPosition, size);
    }

    /**
     * Picks a random element out of any array type.
     * 
     * @param array
     *            the array to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(T[] array) {
        return array[(int) (Math.random() * array.length)];
    }

    /**
     * Picks a random element out of any list type.
     * 
     * @param list
     *            the list to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(List<T> list) {
        return list.get((int) (Math.random() * list.size()));
    }

    /**
     * Executes a method from the specified class by its name, assuming it has
     * no parameters.
     * 
     * @param methodName
     *            the method to execute.
     * @param classWithMethod
     *            the class with the method you want to execute.
     */
    public void classMethod(String methodName, Class<?> classWithMethod) {
        for (Method m : classWithMethod.getMethods()) {
            if (m == null || !Modifier.isStatic(m.getModifiers())) {
                continue;
            }

            if (m.getName().equals(methodName)) {
                try {
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Scans the list of unfriendly hosts from an external text file using a
     * {@link Scanner}.
     * 
     * @throws Exception
     *             if any errors occur during the coding of hosts.
     */
    public static void codeHosts() throws Exception {
        HostGateway.getDisabledHosts().clear();
        Scanner scanner = new Scanner(new File("./data/host_coder.txt"));

        try {
            int parsed = 0;

            while (scanner.hasNextLine()) {
                HostGateway.getDisabledHosts().add(scanner.nextLine());
                parsed++;
            }
        } finally {
            scanner.close();
        }
    }

    /**
     * Scans and loads dialogues, skills, packets, and minigames.
     * 
     * @throws Exception
     *             if any errors occur during the coding of files.
     */
    public static void codeFiles() throws Exception {
        SkillEvent.getSkillEvents().clear();
        PacketDecoder.clear();

        Scanner scanner = new Scanner(new File("./data/file_coder.txt"));
        int parsed = 0;

        try {
            while (scanner.hasNextLine()) {
                String keyword = scanner.next();
                String path = scanner.next();

                if (keyword.equals("#skill")) {
                    Class<?> file = Class.forName(path);

                    if (!(file.getSuperclass() == SkillEvent.class)) {
                        throw new IllegalStateException(
                                "Illegal skill! Not an instance of SkillEvent: "
                                        + path);
                    }

                    SkillEvent.getSkillEvents().add(
                            (SkillEvent) file.newInstance());
                    parsed++;
                } else if (keyword.equals("#packet")) {
                    Class<?> file = Class.forName(path);

                    if (!(file.getSuperclass() == PacketDecoder.class)) {
                        throw new IllegalStateException(
                                "Illegal packet decoder! Not an instance of PacketDecoder: "
                                        + path);
                    }

                    PacketDecoder
                            .addDecoder((PacketDecoder) file.newInstance());
                    parsed++;
                } else if (keyword.equals("#minigame")) {
                    Object file = Class.forName(path);

                    if (!(file instanceof Minigame)) {
                        throw new IllegalStateException(
                                "Illegal minigame! Not an instance of Minigame: "
                                        + path);
                    }

                    Minigame minigame = (Minigame) file;
                    MinigameFactory.getMinigames().put(minigame.name(),
                            minigame);
                    parsed++;
                }
            }
        } finally {
            scanner.close();
        }

        // logger.info("Coded " + parsed + " file utilities!");
    }

    /**
     * Loads and spawns npc on startup.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadWorldNpcs() throws Exception {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/npcs/world_npcs.json")));
        final Gson builder = new GsonBuilder().create();

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int id = reader.get("npc-id").getAsInt();
            Position position = builder.fromJson(reader.get("position")
                    .getAsJsonObject(), Position.class);
            Coordinator coordinator = builder.fromJson(
                    reader.get("walking-policy").getAsJsonObject(),
                    Coordinator.class);

            if (coordinator.isCoordinate() && coordinator.getRadius() == 0) {
                throw new IllegalStateException(
                        "Radius must be higher than 0 when coordinator is active!");
            } else if (!coordinator.isCoordinate()
                    && coordinator.getRadius() > 0) {
                throw new IllegalStateException(
                        "Radius must be 0 when coordinator is inactive!");
            }

            Npc npc = new Npc(id, position);
            npc.getMovementCoordinator().setCoordinator(coordinator);
            npc.setRespawn(true);
            World.getNpcs().add(npc);
        }
    }

    /**
     * Loads all of the shops from the <code>world_shops.json</code> file.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadShops() throws Exception {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/shops/world_shops.json")));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            Shop shop = new Shop(reader.get("id").getAsInt(), reader
                    .get("name").getAsString(), builder.fromJson(
                    reader.get("items").getAsJsonArray(), Item[].class), reader
                    .get("restock").getAsBoolean(), reader
                    .get("can-sell-items").getAsBoolean(),
                    Currency.valueOf(reader.get("currency").getAsString()));

            for (int e : NO_SHOP_ITEMS) {
                if (shop.getShopContainer().contains(e)) {
                    throw new IllegalStateException("Invalid shop item: "
                            + ItemDefinition.getDefinitions()[e].getItemName());
                }
            }

            Shop.getShops()[shop.getIndex()] = shop;
            parsed++;
        }
    }

    /**
     * Parse the npc definitions.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadNpcDefinitions() throws Exception {
        NpcDefinition.setNpcDefinition(new NpcDefinition[6102]);

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/npcs/npc_definitions.json")));
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int index = reader.get("id").getAsInt();

            NpcDefinition.getNpcDefinition()[index] = new NpcDefinition();
            NpcDefinition.getNpcDefinition()[index].setId(index);
            NpcDefinition.getNpcDefinition()[index].setName(reader.get("name")
                    .getAsString());
            NpcDefinition.getNpcDefinition()[index].setExamine(reader.get(
                    "examine").getAsString());
            NpcDefinition.getNpcDefinition()[index].setCombatLevel(reader.get(
                    "combat").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setNpcSize(reader.get(
                    "size").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setAttackable(reader.get(
                    "attackable").getAsBoolean());
            NpcDefinition.getNpcDefinition()[index].setAggressive(reader.get(
                    "aggressive").getAsBoolean());
            NpcDefinition.getNpcDefinition()[index].setRetreats(reader.get(
                    "retreats").getAsBoolean());
            NpcDefinition.getNpcDefinition()[index].setPoisonous(reader.get(
                    "poisonous").getAsBoolean());
            NpcDefinition.getNpcDefinition()[index].setRespawnTime(reader.get(
                    "respawn").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setMaxHit(reader.get(
                    "maxHit").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setHitpoints(reader.get(
                    "hitpoints").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setAttackSpeed(reader.get(
                    "attackSpeed").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setAttackAnimation(reader
                    .get("attackAnim").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setDefenceAnimation(reader
                    .get("defenceAnim").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setDeathAnimation(reader
                    .get("deathAnim").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setAttackBonus(reader.get(
                    "attackBonus").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setDefenceMelee(reader.get(
                    "defenceMelee").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setDefenceRange(reader.get(
                    "defenceRange").getAsInt());
            NpcDefinition.getNpcDefinition()[index].setDefenceMage(reader.get(
                    "defenceMage").getAsInt());
            parsed++;
        }
    }

    /**
     * Parse the item definitions.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadItemDefinitions() throws JsonIOException,
            JsonSyntaxException, FileNotFoundException {
        ItemDefinition.setDefinitions(new ItemDefinition[7956]);

        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/items/item_definitions.json")));
        final Gson builder = new GsonBuilder().create();
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int index = reader.get("id").getAsInt();

            ItemDefinition.getDefinitions()[index] = new ItemDefinition();
            ItemDefinition.getDefinitions()[index].setItemId(index);
            ItemDefinition.getDefinitions()[index].setItemName(reader.get(
                    "name").getAsString());
            ItemDefinition.getDefinitions()[index].setItemDescription(reader
                    .get("examine").getAsString());
            ItemDefinition.getDefinitions()[index].setEquipmentSlot(reader.get(
                    "equipmentType").getAsInt());
            ItemDefinition.getDefinitions()[index].setNoted(reader.get("noted")
                    .getAsBoolean());
            ItemDefinition.getDefinitions()[index].setNoteable(reader.get(
                    "noteable").getAsBoolean());
            ItemDefinition.getDefinitions()[index].setStackable(reader.get(
                    "stackable").getAsBoolean());
            ItemDefinition.getDefinitions()[index].setUnNotedId(reader.get(
                    "parentId").getAsInt());
            ItemDefinition.getDefinitions()[index].setNotedId(reader.get(
                    "notedId").getAsInt());
            ItemDefinition.getDefinitions()[index].setMembersItem(reader.get(
                    "members").getAsBoolean());
            ItemDefinition.getDefinitions()[index].setSpecialStorePrice(reader
                    .get("specialStorePrice").getAsInt());
            ItemDefinition.getDefinitions()[index].setGeneralStorePrice(reader
                    .get("generalStorePrice").getAsInt());
            ItemDefinition.getDefinitions()[index].setHighAlchValue(reader.get(
                    "highAlchValue").getAsInt());
            ItemDefinition.getDefinitions()[index].setLowAlchValue(reader.get(
                    "lowAlchValue").getAsInt());
            ItemDefinition.getDefinitions()[index].setWeight(reader.get(
                    "weight").getAsDouble());
            ItemDefinition.getDefinitions()[index].setBonus(builder.fromJson(
                    reader.get("bonuses").getAsJsonArray(), int[].class));
            parsed++;
        }
    }

    /**
     * Formats the price for easier viewing.
     * 
     * @param price
     *            the price to format.
     * @return the newly formatted price.
     */
    public static String formatPrice(int price) {
        if (price >= 1000 && price < 1000000) {
            return " (" + (price / 1000) + "K)";
        } else if (price >= 1000000) {
            return " (" + (price / 1000000) + " million)";
        }

        return "";
    }

    /**
     * Parse the all of the data for the world objects.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadWorldObjects() throws Exception {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/objects/world_objects.json")));
        int parsed = 0;

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            int id = reader.get("id").getAsInt();
            int x = reader.get("x").getAsInt();
            int y = reader.get("y").getAsInt();
            int z = reader.get("z").getAsInt();
            Rotation face = Rotation.valueOf(reader.get("rotation")
                    .getAsString());

            if (face == null) {
                throw new IllegalStateException(
                        "Invalid object rotation! for [" + id + ":" + x + ":"
                                + y + ":" + z + "]");
            }

            int type = reader.get("type").getAsInt();

            World.getObjects()
                    .getObjectSet()
                    .add(new WorldObject(id, new Position(x, y, z), face, type));
            parsed++;
        }
    }

    /**
     * Parse the all of the data for the static world items.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadWorldItems() throws Exception {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/items/world_items.json")));

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            StaticGroundItem item = new StaticGroundItem(new Item(reader.get(
                    "id").getAsInt(), reader.get("amount").getAsInt()),
                    new Position(reader.get("x").getAsInt(), reader.get("y")
                            .getAsInt(), reader.get("z").getAsInt()), false,
                    reader.get("respawns").getAsBoolean());
            World.getGroundItems().register(item);
        }
    }

    /**
     * Parse the all of the data for npc drops.
     * 
     * @throws Exception
     *             if any errors occur while parsing this file.
     */
    public static void loadNpcDrops() throws Exception {
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse(new FileReader(new File(
                "./data/json/npcs/world_npc_drops.json")));
        final Gson builder = new GsonBuilder().create();

        for (int i = 0; i < array.size(); i++) {
            JsonObject reader = (JsonObject) array.get(i);

            final int id[] = builder.fromJson(reader.get("id"), int[].class);
            final NpcDrop[] dynamic = builder.fromJson(reader.get("dynamic"),
                    NpcDrop[].class);
            final NpcDrop[] rare = builder.fromJson(reader.get("rare"),
                    NpcDrop[].class);

            for (int e : id) {
                NpcDropTable.getAllDrops().put(e,
                        new NpcDropTable(id, dynamic, rare));
            }
        }
    }

    /**
     * Converts a long to a string. Used for private messaging.
     * 
     * @param l
     *            the long.
     * @return the string.
     */
    public static String longToName(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    /**
     * Loads all equipment properties.
     * 
     * @throws Exception
     *             if any errors occur while scanningthis file.
     */
    public static void codeEquipment() throws Exception {
        Scanner scanner = new Scanner(new File("./data/equipment_coder.txt"));
        int parsed = 0;

        try {
            while (scanner.hasNextLine()) {
                String keyword = scanner.next();
                int value = scanner.nextInt();

                if (keyword.equals("#2h")) {
                    is2H.add(value);
                    parsed++;
                } else if (keyword.equals("#full_helm")) {
                    isFullHelm.add(value);
                    parsed++;
                } else if (keyword.equals("#platebody")) {
                    isPlatebody.add(value);
                    parsed++;
                }
            }
        } finally {
            scanner.close();
        }

        // logger.info("Coded " + parsed + " equipment utilities!");
    }

    /**
     * Constants used for probability operations.
     * 
     * @author lare96
     */
    public enum Chance {
        ALWAYS(100),

        VERY_COMMON(90),

        COMMON(75),

        SOMETIMES(50),

        UNCOMMON(35),

        VERY_UNCOMMON(10),

        EXTREMELY_RARE(5),

        ALMOST_IMPOSSIBLE(1);

        /** The percentage of this constant. */
        private int percentage;

        /**
         * Creates a new {@link Chance}.
         * 
         * @param percentage
         *            the percentage of this constant.
         */
        Chance(int percentage) {
            this.percentage = percentage;
        }

        /**
         * Calculates success based on the underlying chance.
         * 
         * @return true if it was successful.
         */
        public boolean success() {
            return (random(100) + 1) <= percentage;
        }

        /**
         * Gets the percentage of this constant.
         * 
         * @return the percentage.
         */
        public int getPercentage() {
            return percentage;
        }
    }

    /**
     * Converts an array of bytes to an integer.
     * 
     * @param data
     *            the array of bytes.
     * @return the newly constructed integer.
     */
    public static int hexToInt(byte[] data) {
        int value = 0;
        int n = 1000;
        for (int i = 0; i < data.length; i++) {
            int num = (data[i] & 0xFF) * n;
            value += num;
            if (n > 1) {
                n = n / 1000;
            }
        }
        return value;
    }

    /**
     * Unpacks text from an array of bytes.
     * 
     * @param packedData
     *            the array of bytes.
     * @param size
     *            the size of the array of bytes.
     * @return the unpacked string.
     */
    public static String textUnpack(byte packedData[], int size) {
        int idx = 0, highNibble = -1;
        for (int i = 0; i < size * 2; i++) {
            int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
            if (highNibble == -1) {
                if (val < 13)
                    decodeBuf[idx++] = xlateTable[val];
                else
                    highNibble = val;
            } else {
                decodeBuf[idx++] = xlateTable[((highNibble << 4) + val) - 195];
                highNibble = -1;
            }
        }

        return new String(decodeBuf, 0, idx);
    }

    /**
     * Reads a string from an i/o stream.
     * 
     * @param input
     *            the i/o stream.
     * @return the string read.
     */
    public static String readInputString(DataInputStream input) {
        byte data;
        StringBuilder builder = new StringBuilder();
        try {
            while ((data = input.readByte()) != 0) {
                builder.append((char) data);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * Formats a string.
     * 
     * @param string
     *            the string formatted.
     * @return the newly formatted string.
     */
    public static String formatInputString(String string) {
        String result = "";
        for (String part : string.toLowerCase().split(" ")) {
            result += part.substring(0, 1).toUpperCase() + part.substring(1)
                    + " ";
        }
        return result.trim();
    }

    /**
     * Converts a string to a long value.
     * 
     * @param s
     *            the string.
     * @return the long value.
     */
    public static long nameToLong(String s) {
        long l = 0L;
        for (int i = 0; i < s.length() && i < 12; i++) {
            char c = s.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }

    /**
     * Returns the delta coordinates. Note that the returned Position is not an
     * actual position, instead it's values represent the delta values between
     * the two arguments.
     * 
     * @param a
     *            the first position.
     * @param b
     *            the second position.
     * @return the delta coordinates contained within a position.
     */
    public static Position delta(Position a, Position b) {
        return new Position(b.getX() - a.getX(), b.getY() - a.getY());
    }

    /**
     * Calculates the direction between the two coordinates.
     * 
     * @param dx
     *            the first coordinate.
     * @param dy
     *            the second coordinate.
     * @return the direction.
     */
    public static int direction(int dx, int dy) {
        if (dx < 0) {
            if (dy < 0) {
                return 5;
            } else if (dy > 0) {
                return 0;
            } else {
                return 3;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                return 7;
            } else if (dy > 0) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (dy < 0) {
                return 6;
            } else if (dy > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /** Lengths for the various packets. */
    public static final int packetLengths[] = { //
    0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
            0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
            0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
            0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
            2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
            0, 0, 0, 12, 0, 0, 0, 0, 8, 0, // 50
            0, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
            6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
            0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
            0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
            0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
            0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
            1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
            0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
            0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
            0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
            0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
            0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
            0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
            2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
            4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
            0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
            1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
            0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
            0, 0, 6, 6, 0, 0, 0 // 250
    };

    /**
     * A simple timing utility.
     * 
     * @author blakeman8192
     * @author lare96
     */
    public static class Stopwatch {

        /** The cached time. */
        private long time = System.currentTimeMillis();

        /**
         * Resets with a head start option.
         * 
         * @param startAt
         *            the head start value.
         */
        public Stopwatch headStart(long startAt) {
            time = System.currentTimeMillis() - startAt;
            return this;
        }

        /**
         * Resets this stopwatch.
         * 
         * @return this stopwatch.
         */
        public Stopwatch reset() {
            time = System.currentTimeMillis();
            return this;
        }

        /**
         * Returns the amount of time elapsed (in milliseconds) since this
         * object was initialized, or since the last call to the "reset()"
         * method.
         * 
         * @return the elapsed time (in milliseconds).
         */
        public long elapsed() {
            return System.currentTimeMillis() - time;
        }
    }

    /**
     * An inclusive or exclusive interval.
     * 
     * @author lare96
     */
    public static class Interval {

        /** The starting point. */
        private int start;

        /** The ending point. */
        private int end;

        /**
         * Creates a new inclusive {@link Interval}.
         * 
         * @param start
         *            the starting point.
         * @param end
         *            the ending point.
         * @return the inclusive interval.
         */
        public Interval inclusiveInterval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException(
                        "End value must be higher than start value!");
            }

            this.start = start;
            this.end = end;
            return this;
        }

        /**
         * Creates a new exclusive {@link Interval}.
         * 
         * @return the exclusive interval.
         */
        public Interval exclusiveInterval(int start, int end) {
            if (start > end) {
                throw new IllegalArgumentException(
                        "End value must be higher than start value!");
            }

            this.start = start + 1;
            this.end = end - 1;
            return this;
        }

        /**
         * Gets a random value based on the interval.
         * 
         * @return the random value.
         */
        public int calculate() {
            int difference = end - start;

            return (start + random(difference));
        }

        /**
         * The starting point.
         * 
         * @return the starting point.
         */
        public int getStart() {
            return start;
        }

        /**
         * The ending point.
         * 
         * @return the ending point.
         */
        public int getEnd() {
            return end;
        }
    }

    /**
     * A container that contains a single runnable method which can take any
     * type as its parameter.
     * 
     * @author lare96
     * @param <T>
     *            the type to use as the parameter.
     */
    public static interface GenericAction<T> {

        /**
         * The action that will be executed.
         * 
         * @param type
         *            the type that will be used as the parameter.
         * @return {@code true} if and only if the action executed successfully,
         *         otherwise {@code false}.
         */
        public void fireAction(final T type);
    }

    /**
     * Thread local random instance, used to generate pseudo-random primitive
     * types. Thread local random is faster than your traditional random
     * implementation as there is no unnecessary wait on the backing
     * <code>AtomicLong</code> within {@link Random}.
     */
    public static final Random RANDOM = ThreadLocalRandom.current();

    /**
     * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
     * and exclusive <code>range</code>.
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If the specified range is less <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int randomNoZero(int range) {
        if (range < 0) {
            throw new IllegalArgumentException("range < 0");
        }

        int r = RANDOM.nextInt(range);

        if (r == 0)
            r++;
        return r;
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive <tt>0</tt>
     * and exclusive <code>range</code>.
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If the specified range is less <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code int}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static int random(int range) {
        if (range < 0) {
            throw new IllegalArgumentException("range < 0");
        }

        return RANDOM.nextInt(range);
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code>.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #random(int)}.
     */
    public static int inclusiveRandom(int min, int max) {
        return random((max - min) + 1) + min;
    }

    /**
     * Returns a pseudo-random {@code int} value between inclusive
     * <code>min</code> and inclusive <code>max</code> excluding the specified
     * numbers within the {@code excludes} array.
     * 
     * @param min
     *            The minimum inclusive number.
     * @param max
     *            The maximum inclusive number.
     * @return The pseudo-random {@code int}.
     * @throws IllegalArgumentException
     *             If {@code max - min + 1} is less than <tt>0</tt>.
     * @see {@link #inclusiveRandom(int, int)}.
     */
    public static int inclusiveRandomExcludes(int min, int max, int... exclude) {
        Arrays.sort(exclude);

        int result = inclusiveRandom(min, max);
        while (Arrays.binarySearch(exclude, result) >= 0) {
            result = inclusiveRandom(min, max);
        }

        return result;
    }

    /**
     * Returns a pseudo-random {@code float} between inclusive <tt>0</tt> and
     * exclusive <code>range</code>
     * 
     * <br>
     * <br>
     * This method is thread-safe. </br>
     * 
     * @param range
     *            The exclusive range.
     * @return The pseudo-random {@code float}.
     * @throws IllegalArgumentException
     *             If the specified range is less than <tt>0</tt>
     * 
     *             <p>
     *             We use {@link ThreadLocalRandom#current()} to produce this
     *             random {@code float}, it is faster than a standard
     *             {@link Random} instance as we do not have to wait on
     *             {@code AtomicLong}.
     *             </p>
     */
    public static float random(float range) {
        if (range < 0F) {
            throw new IllegalArgumentException("range <= 0");
        }

        return RANDOM.nextFloat() * range;
    }

    /**
     * Gets the array of platebodies.
     * 
     * @return the array of platebodies.
     */
    public static Set<Integer> getIsPlatebody() {
        return isPlatebody;
    }

    /**
     * Gets the array of full helms.
     * 
     * @return the array of full helms.
     */
    public static Set<Integer> getIsFullHelm() {
        return isFullHelm;
    }

    /**
     * Gets the array of two handed swords.
     * 
     * @return the array of two handed swords.
     */
    public static Set<Integer> getIs2H() {
        return is2H;
    }
}
