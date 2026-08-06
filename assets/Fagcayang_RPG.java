import java.util.*;
import java.io.*;

class Storyteller {

    // ---- All input now flows through a single shared reader ------------------------------------
    static BufferedReader sharedReader;

    static void tell(String text) {
        System.out.println("\n" + "=".repeat(75));
        System.out.println(text);
        System.out.println("=".repeat(75));
        pressToContinue();
    }

    static void cutscene(String text) {
        System.out.println("\n>>> --- NARRATIVE PROGRESSION: MEMORY UNLOCKED --- <<<");
        System.out.println(text);
        System.out.println(">>> ---------------------------------------------- <<<");
        pressToContinue();
    }

    static void pressToContinue() {
        System.out.println("\n[Press Enter to continue the tale...]");
        try {
            // Use the shared BufferedReader instead of raw System.in
            // so the input stream is never mixed between two readers.
            if (sharedReader != null) {
                sharedReader.readLine();   // blocks until the user hits Enter
            }
        } catch (IOException ex) {
            // Stream interrupted - continue gracefully
        }
    }

    static void action(String text) {
        System.out.println("- " + text);
    }
}

/* Dialogue System */
class Dialogue {
    public static int choose(String speaker, String line, String... options) {
        System.out.println("\n[" + speaker + "]");
        System.out.println(line + "\n");

        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }

        int choice = -1;
        while (choice < 1 || choice > options.length) {
            System.out.print("Choose (1-" + options.length + "): ");
            try {
                String raw = Fagcayang_RPG.reader.readLine();
                if (raw == null) raw = "";
                raw = raw.trim();
                choice = Integer.parseInt(raw);
            } catch (NumberFormatException | IOException ex) {
                System.out.println("  [Please enter a number between 1 and " + options.length + ".]");
            }
        }
        return choice;
    }

    public static void say(String speaker, String line) {
        System.out.println("\n[" + speaker + "] " + line);
    }
}

/* Core RPG Classes */
class Equipment {
    private String weaponName;
    private int powerBoost;

    public Equipment(String name, int boost) {
        this.weaponName = name;
        this.powerBoost = boost;
    }

    public int getPowerBoost() { return powerBoost; }
}

interface SpecialMove {
    String getName();
    String getDescription();
    void use(Character user, Character enemy);
}

abstract class Character {
    protected String name, job;
    protected int health, damage, stamina, energy = 0;
    protected Equipment weapon;
    protected List<SpecialMove> activeMoves = new ArrayList<>();
    protected List<SpecialMove> movePool   = new ArrayList<>();
    protected SpecialMove ultimate;

    // Light attack stamina cost = 10, Heavy attack stamina cost = 25
    protected static final int LIGHT_COST = 10;
    protected static final int HEAVY_COST = 25;

    public Character(String name, String job, int health, int damage, int stamina, Equipment weapon) {
        this.name    = name;
        this.job     = job;
        this.health  = health;
        this.damage  = damage + (weapon != null ? weapon.getPowerBoost() : 0);
        this.stamina = stamina;
        this.weapon  = weapon;
    }

    public void takeDamage(int amt) { this.health -= amt; }
    public void gainEnergy()        { this.energy = Math.min(this.energy + 34, 100); }

    /** Regenerate 5 stamina every turn - called at end of each turn */
    public void regenStamina()      { this.stamina += 5; }

    public boolean canUse(int cost) { return stamina >= cost; }
    public void useStamina(int cost){ stamina -= cost; }

    /** Light attack: lower damage, costs LIGHT_COST stamina */
    public void lightAttack(Character enemy) {
        if (!canUse(LIGHT_COST)) {
            System.out.println("  [Not enough stamina for a light attack! Skipping...]");
            return;
        }
        useStamina(LIGHT_COST);
        enemy.takeDamage(this.damage);
        Storyteller.action(name + " performs a swift light attack for " + this.damage + " damage!");
    }

    /** Heavy attack: higher damage (1.8x), costs HEAVY_COST stamina */
    public void heavyAttack(Character enemy) {
        if (!canUse(HEAVY_COST)) {
            System.out.println("  [Not enough stamina for a heavy attack! Skipping...]");
            return;
        }
        useStamina(HEAVY_COST);
        int heavyDmg = (int)(this.damage * 1.8);
        enemy.takeDamage(heavyDmg);
        Storyteller.action(name + " winds up and delivers a crushing heavy attack for " + heavyDmg + " damage!");
    }

    /** Legacy basic attack kept for compatibility */
    public void basicAttack(Character enemy) {
        lightAttack(enemy);
    }

    public String getName()   { return name; }
    public int getHealth()    { return health; }
    public int getDamage()    { return damage; }
    public int getStamina()   { return stamina; }
}

/* --------------------------------------------------------------------------------------------------------------------------
   WARRIOR - high HP & stamina, melee-focused
   -------------------------------------------------------------------------------------------------------------------------- */
class Warrior extends Character {

    public Warrior(String name) {
        super(name, "Warrior", 400, 35, 120, new Equipment("Greatsword of Aethelheart", 20));

        movePool.add(new SpecialMove() {
            public String getName()        { return "Shield Bash"; }
            public String getDescription() { return "slams with a powerful shield bash, staggering the foe!"; }
            public void use(Character u, Character e) {
                if (!u.canUse(15)) { System.out.println("  [Not enough stamina! Skipping Shield Bash...]"); return; }
                u.useStamina(15);
                e.takeDamage(u.getDamage() + 30);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Whirlwind Slash"; }
            public String getDescription() { return "spins in a deadly arc, cutting down everything nearby!"; }
            public void use(Character u, Character e) {
                if (!u.canUse(20)) { System.out.println("  [Not enough stamina! Skipping Whirlwind Slash...]"); return; }
                u.useStamina(20);
                e.takeDamage(u.getDamage() + 50);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Iron Fortress"; }
            public String getDescription() { return "braces with iron will, halving the next blow!"; }
            public void use(Character u, Character e) {
                if (!u.canUse(10)) { System.out.println("  [Not enough stamina! Skipping Iron Fortress...]"); return; }
                u.useStamina(10);
                Storyteller.action(u.getName() + " enters a defensive stance!");
            }
        });

        ultimate = new SpecialMove() {
            public String getName()        { return "Titan's Reckoning"; }
            public String getDescription() { return "channels the spirit of ancient titans and delivers a catastrophic blow!"; }
            public void use(Character u, Character e) {
                e.takeDamage(500);
            }
        };
    }
}

/* --------------------------------------------------------------------------------------------------------------------------
   ARCHER - balanced, uses ARROWS as the resource limiter
   -------------------------------------------------------------------------------------------------------------------------- */
class Archer extends Character {
    private int arrows;

    public Archer(String name) {
        super(name, "Archer", 300, 38, 100, new Equipment("Elven Recurve Bow", 15));
        this.arrows = 20;

        movePool.add(new SpecialMove() {
            public String getName()        { return "Piercing Shot"; }
            public String getDescription() { return "looses a perfectly aimed arrow that pierces through armor!"; }
            public void use(Character u, Character e) {
                Archer a = (Archer) u;
                if (a.arrows <= 0) { System.out.println("  [No arrows left! Skipping Piercing Shot...]"); return; }
                if (!u.canUse(15)) { System.out.println("  [Not enough stamina! Skipping Piercing Shot...]"); return; }
                a.arrows--;
                u.useStamina(15);
                e.takeDamage(u.getDamage() + 35);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Volley Fire"; }
            public String getDescription() { return "releases a rapid volley of three arrows in quick succession!"; }
            public void use(Character u, Character e) {
                Archer a = (Archer) u;
                if (a.arrows < 3) { System.out.println("  [Not enough arrows (need 3)! Skipping Volley Fire...]"); return; }
                if (!u.canUse(20)) { System.out.println("  [Not enough stamina! Skipping Volley Fire...]"); return; }
                a.arrows -= 3;
                u.useStamina(20);
                e.takeDamage(u.getDamage() + 60);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Smoke Arrow"; }
            public String getDescription() { return "fires a smoke arrow, confusing and briefly blinding the enemy!"; }
            public void use(Character u, Character e) {
                Archer a = (Archer) u;
                if (a.arrows <= 0) { System.out.println("  [No arrows left! Skipping Smoke Arrow...]"); return; }
                if (!u.canUse(10)) { System.out.println("  [Not enough stamina! Skipping Smoke Arrow...]"); return; }
                a.arrows--;
                u.useStamina(10);
                Storyteller.action(u.getName() + " blinds the enemy with smoke!");
            }
        });

        ultimate = new SpecialMove() {
            public String getName()        { return "Rain of a Thousand Arrows"; }
            public String getDescription() { return "calls down a legendary barrage that blots out the sky!"; }
            public void use(Character u, Character e) {
                Archer a = (Archer) u;
                a.arrows = Math.max(0, a.arrows - 10);
                e.takeDamage(480);
            }
        };
    }

    @Override
    public void lightAttack(Character enemy) {
        if (arrows <= 0) {
            System.out.println("  [No arrows! Skipping light attack...]");
            return;
        }
        if (!canUse(LIGHT_COST)) {
            System.out.println("  [Not enough stamina for a light attack! Skipping...]");
            return;
        }
        arrows--;
        useStamina(LIGHT_COST);
        enemy.takeDamage(this.damage);
        Storyteller.action(name + " fires a quick arrow for " + this.damage + " damage! (" + arrows + " arrows left)");
    }

    @Override
    public void heavyAttack(Character enemy) {
        if (arrows < 2) {
            System.out.println("  [Not enough arrows (need 2)! Skipping heavy attack...]");
            return;
        }
        if (!canUse(HEAVY_COST)) {
            System.out.println("  [Not enough stamina for a heavy attack! Skipping...]");
            return;
        }
        arrows -= 2;
        useStamina(HEAVY_COST);
        int heavyDmg = (int)(this.damage * 1.8);
        enemy.takeDamage(heavyDmg);
        Storyteller.action(name + " draws back fully and releases a power shot for " + heavyDmg + " damage! (" + arrows + " arrows left)");
    }

    public int getArrows() { return arrows; }

    public void restoreArrows(int amount) { arrows = Math.min(arrows + amount, 20); }
}

/* --------------------------------------------------------------------------------------------------------------------------
   MAGE - Elemental Mage, commands fire, ice, and lightning.
   Uses MANA as the resource limiter.
   -------------------------------------------------------------------------------------------------------------------------- */
class Mage extends Character {
    private int mana;
    private static final int MAX_MANA = 120;

    public Mage(String name) {
        super(name, "Elemental Mage", 240, 48, 80, new Equipment("Staff of the Four Winds", 20));
        this.mana = MAX_MANA;

        movePool.add(new SpecialMove() {
            public String getName()        { return "Pyroclast"; }
            public String getDescription() { return "hurls a superheated sphere of fire that detonates on impact!"; }
            public void use(Character u, Character e) {
                Mage m = (Mage) u;
                if (m.mana < 18) { System.out.println("  [Not enough mana! Skipping Pyroclast...]"); return; }
                if (!u.canUse(10)) { System.out.println("  [Not enough stamina! Skipping Pyroclast...]"); return; }
                m.mana -= 18;
                u.useStamina(10);
                e.takeDamage(u.getDamage() + 45);
                Storyteller.action("The air ignites. Embers spiral outward from the impact.");
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Frost Nova"; }
            public String getDescription() { return "erupts in a burst of ice that freezes the enemy in place!"; }
            public void use(Character u, Character e) {
                Mage m = (Mage) u;
                if (m.mana < 25) { System.out.println("  [Not enough mana! Skipping Frost Nova...]"); return; }
                if (!u.canUse(15)) { System.out.println("  [Not enough stamina! Skipping Frost Nova...]"); return; }
                m.mana -= 25;
                u.useStamina(15);
                e.takeDamage(u.getDamage() + 60);
                Storyteller.action("Ice crystallizes across the enemy's limbs. They stagger.");
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Chain Lightning"; }
            public String getDescription() { return "calls down a bolt of lightning that arcs with searing voltage!"; }
            public void use(Character u, Character e) {
                Mage m = (Mage) u;
                if (m.mana < 30) { System.out.println("  [Not enough mana! Skipping Chain Lightning...]"); return; }
                if (!u.canUse(18)) { System.out.println("  [Not enough stamina! Skipping Chain Lightning...]"); return; }
                m.mana -= 30;
                u.useStamina(18);
                e.takeDamage(u.getDamage() + 80);
                Storyteller.action("Thunder cracks. The smell of ozone fills the air.");
            }
        });

        ultimate = new SpecialMove() {
            public String getName()        { return "Convergence: Elemental Cataclysm"; }
            public String getDescription() { return "merges fire, ice, and lightning into one catastrophic eruption!"; }
            public void use(Character u, Character e) {
                Mage m = (Mage) u;
                m.mana = Math.max(0, m.mana - 60);
                e.takeDamage(520);
                Storyteller.action(
                    "Fire. Ice. Lightning. All three converge at a single point -\n" +
                    "  then tear outward in every direction at once.\n" +
                    "  The air itself does not know which way to flee."
                );
            }
        };
    }

    @Override
    public void lightAttack(Character enemy) {
        if (mana < 8) {
            System.out.println("  [Not enough mana! Skipping elemental strike...]");
            return;
        }
        if (!canUse(LIGHT_COST)) {
            System.out.println("  [Not enough stamina for a light attack! Skipping...]");
            return;
        }
        mana -= 8;
        useStamina(LIGHT_COST);
        enemy.takeDamage(this.damage);
        Storyteller.action(name + " hurls a quick elemental strike for " + this.damage + " damage! (" + mana + " mana left)");
    }

    @Override
    public void heavyAttack(Character enemy) {
        if (mana < 20) {
            System.out.println("  [Not enough mana (need 20)! Skipping elemental blast...]");
            return;
        }
        if (!canUse(HEAVY_COST)) {
            System.out.println("  [Not enough stamina for a heavy attack! Skipping...]");
            return;
        }
        mana -= 20;
        useStamina(HEAVY_COST);
        int heavyDmg = (int)(this.damage * 1.8);
        enemy.takeDamage(heavyDmg);
        Storyteller.action(name + " channels the elements into a focused blast for " + heavyDmg + " damage! (" + mana + " mana left)");
    }

    public int getMana()    { return mana; }
    public int getMaxMana() { return MAX_MANA; }

    public void restoreMana(int amount) {
        mana = Math.min(mana + amount, MAX_MANA);
    }
}

/* --------------------------------------------------------------------------------------------------------------------------
   ROGUE - high stamina, shadow mechanics
   -------------------------------------------------------------------------------------------------------------------------- */
class Rogue extends Character {
    private boolean shadowBuff = false;

    public Rogue(String name) {
        super(name, "Rogue", 260, 40, 130, new Equipment("Twin Shadow Daggers", 18));

        movePool.add(new SpecialMove() {
            public String getName()        { return "Stealth Strike"; }
            public String getDescription() { return "fades into shadows and strikes before being seen!"; }
            public void use(Character u, Character e) {
                shadowBuff = true;
                e.takeDamage(u.getDamage() + 20);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Heartstab"; }
            public String getDescription() { return "drives the blade toward the enemy's heart with lethal precision!"; }
            public void use(Character u, Character e) {
                e.takeDamage(u.getDamage() + 90);
            }
        });

        movePool.add(new SpecialMove() {
            public String getName()        { return "Umbral Dominion"; }
            public String getDescription() { return "summons shadow echoes that empower your next actions!"; }
            public void use(Character u, Character e) {
                shadowBuff = true;
            }
        });

        ultimate = new SpecialMove() {
            public String getName()        { return "Eclipse of a Hundred Blades"; }
            public String getDescription() { return "vanishes completely-then unleashes a storm of invisible slashes!"; }
            public void use(Character u, Character e) {
                e.takeDamage(450);
            }
        };
    }

    @Override
    public void takeDamage(int amt) {
        if (shadowBuff) {
            amt = (int)(amt * 0.6);
            shadowBuff = false;
            Storyteller.action(name + " slips through shadows, reducing damage!");
        }
        super.takeDamage(amt);
    }
}

/* --------------------------------------------------------------------------------------------------------------------------
   ENEMY CLASS
   -------------------------------------------------------------------------------------------------------------------------- */
class Enemy extends Character {
    private List<String> attackLines = new ArrayList<>();
    private Random rand = new Random();

    public Enemy(String name, int health, int damage, int stamina) {
        super(name, "Enemy", health, damage, stamina, null);
    }

    public void setAttackLines(String... lines) {
        attackLines.addAll(Arrays.asList(lines));
    }

    public String getAttackLine() {
        if (attackLines.isEmpty()) return "attacks!";
        return attackLines.get(rand.nextInt(attackLines.size()));
    }

    public void takeTurn(Character player) {
        boolean useHeavy = rand.nextBoolean();
        if (useHeavy) {
            heavyAttack(player);
        } else {
            lightAttack(player);
        }
        Storyteller.action(name + " " + getAttackLine());
    }
}

/* --------------------------------------------------------------------------------------------------------------------------
   MAIN
   -------------------------------------------------------------------------------------------------------------------------- */
public class Fagcayang_RPG {

    // ---- Single shared reader used by EVERYTHING (Storyteller + Dialogue + battle) ----
    static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        // Wire the shared reader into Storyteller so pressToContinue()
        // never touches raw System.in again.
        Storyteller.sharedReader = reader;

        boolean playAgain = true;
        while (playAgain) {
            playAgain = runGame();
        }
        System.out.println("\nFarewell, traveler. May your legend endure.");
    }

    static boolean runGame() {
        System.out.println("=== THE LEGEND OF AETHELHEART ===");
        System.out.print("What is your name, traveler? ");
        String name = "";
        try { name = reader.readLine(); } catch (IOException ex) {}
        if (name == null) name = "traveler";
        name = name.trim();
        if (name.isEmpty()) name = "traveler";

        Storyteller.tell("Once upon a time, the village of Aethelgard was a haven of gold and green.\n" +
                "You, " + name + ", lived in a cottage with a blue door. Your mother, Elara,\n" +
                "was the village healer, and your little sister, Miri, loved wildflowers.");

        Storyteller.tell("It was a peaceful morning. You watched Miri braid a crown of clover\n" +
                "for your mother. 'When the harvest comes,' Miri said with a giggle,\n" +
                "'we shall dance until the moon hides.'");

        Storyteller.tell("You left for the Great Forest...\nThe sky ripped open.");

        Storyteller.tell("You sprinted back to find the village burning.\nYour family was gone.");

        Storyteller.tell("Time itself freezes.\n\n" +
                "The flames hang motionless in the air. The screams go silent.\n" +
                "Even the smoke stops moving.\n\n" +
                "Then - light.\n\n" +
                "Not the orange light of fire. Something older. Something that has no name\n" +
                "in any language you have ever spoken.\n\n" +
                "It descends from the sky like a second sunrise, and where it touches the ground\n" +
                "the ash turns to wildflowers.\n\n" +
                "A figure steps out of the radiance.\n" +
                "Tall. Wrapped in light that moves like cloth.\n" +
                "A face you cannot quite hold in your memory - every time you look directly at it,\n" +
                "it seems to shift, as though it is older than the concept of faces.\n\n" +
                "It looks at you.\n" +
                "And somehow, impossibly - it looks sad.");

        int whoAreYou = Dialogue.choose(name,
                "You step back. Your hand reaches for nothing - you have no weapon yet.\n" +
                "The being watches you without moving.",
                "Who are you?! What do you want?",
                "Stay back. Did you do this?",
                "...Are you even real?",
                "What ARE you?");

        switch (whoAreYou) {
            case 1 -> {
                Storyteller.tell("The being tilts its head. When it speaks, the voice comes from\n" +
                        "everywhere at once - not loud, but present, the way silence is present.");
                Dialogue.say("???", "Who am I.\n\nA fair question. Most who see me do not think to ask.\n" +
                        "They run. Or they kneel. You are doing neither.\n" +
                        "I think I already like you, " + name + ".");
            }
            case 2 -> {
                Storyteller.tell("The figure does not flinch. It raises one hand slowly - palm open, no threat.\n" +
                        "The gesture is so calm it makes the burning village feel wrong by comparison.");
                Dialogue.say("???", "I did not do this.\n\n" +
                        "I wish I could say I stopped it.\n" +
                        "I could not. But I am here now - and that is not nothing.");
            }
            case 3 -> {
                Storyteller.tell("The being pauses. It glances down at its own hands as though\n" +
                        "genuinely considering the question.");
                Dialogue.say("???", "Real.\n\nWhat a small word for such a large question.\n" +
                        "The flowers at my feet are real. The fact that you can see me is real.\n" +
                        "Whether I am real in the way you are real...\n" +
                        "that is a conversation for after we have dealt with more urgent matters.");
            }
            case 4 -> {
                Storyteller.tell("Something in the air shifts - not threatening, but vast.\n" +
                        "Like standing at the edge of something with no bottom.");
                Dialogue.say("???", "Something very old.\n\n" +
                        "Something that has watched this world since before it had a name.\n" +
                        "Something that has been waiting for precisely this moment -\n" +
                        "and precisely you.");
            }
        }

        Storyteller.cutscene(
                "The figure steps closer. The light around it dims - not fading, but\n" +
                "condensing, pulling inward, as though it is choosing to be seen more clearly.\n\n" +
                "Features settle. A face forms - ageless, neither young nor old,\n" +
                "neither entirely one thing nor another.\n\n" +
                "It places one hand over its chest and inclines its head.\n\n" +
                "The gesture is unmistakably a bow."
        );

        Dialogue.say("???",
                "I have been called many things across many ages.\n\n" +
                "Guardian. Witness. The First Light. The Keeper Between.\n\n" +
                "But the name I was given at the beginning - the name I still carry -\n" +
                "is Zyraeth.\n\n" +
                "I am the First Luminary.\n" +
                "And I have been looking for you for a very long time, " + name + ".");

        Storyteller.tell("Zyraeth.\n\n" +
                "The name lands differently than you expected.\n" +
                "You have heard it before - carved above the door of every temple in Aethelgard,\n" +
                "spoken every harvest morning by your mother before breakfast,\n" +
                "stitched into the hem of Miri's cloak the day she was born.\n\n" +
                "The name of the one who made the world.\n\n" +
                "You stare at the figure in front of you.\n" +
                "The wildflowers at its feet. The light that still clings to its edges.\n\n" +
                "Oh.");

        int recognize = Dialogue.choose(name,
                "Something clicks into place. Your voice comes out quieter than you intended.",
                "...You are the one they pray to. The Creator.",
                "Every temple. Every prayer. That is YOU standing in front of me.",
                "My mother prayed to you. Every single morning. She prayed to YOU.",
                "Zyraeth... I grew up saying that name. We all did.");

        switch (recognize) {
            case 1 -> {
                Dialogue.say("Zyraeth",
                        "Creator is one word for it.\n" +
                        "I prefer keeper. Shepherd. The one who tends the flame rather than the one who lit it.\n\n" +
                        "But yes, " + name + ". It is me.");
            }
            case 2 -> {
                Dialogue.say("Zyraeth",
                        "Yes.\n\n" +
                        "I know what it is to stand in front of something you have only ever known as an idea.\n" +
                        "Take a moment. It is a great deal to absorb.\n" +
                        "I have given people considerably longer to understand it and they still did not manage.");
            }
            case 3 -> {
                Dialogue.say("Zyraeth",
                        "She prayed well.\n\n" +
                        "Her voice reached me clearly, even on the quiet mornings.\n" +
                        "Elara always found words that were simple and true.\n" +
                        "Those are the prayers that carry farthest.");
                Storyteller.tell("You were not expecting that.\n" +
                        "The idea that Zyraeth - this being made of light and impossible age -\n" +
                        "has been quietly listening to your mother say grace over porridge\n" +
                        "is almost too strange to hold in your head.");
            }
            case 4 -> {
                Dialogue.say("Zyraeth",
                        "As have I. For as long as this village has stood.\n\n" +
                        "I have watched Aethelgard from the beginning, " + name + ".\n" +
                        "Every birth. Every harvest. Every ordinary and extraordinary moment.\n" +
                        "I know this place the way you know a home.");
            }
        }

        Storyteller.tell("It is real.\n\n" +
                "The being in front of you - the light, the flowers, the impossible face -\n" +
                "is exactly who the temples say it is.\n\n" +
                "The one who made this world.\n" +
                "The one everyone in Aethelgard has prayed to since they were old enough to speak.\n\n" +
                "Including your mother.\n" +
                "Including Miri.\n\n" +
                "And the village is still burning.");

        Dialogue.say("Zyraeth",
                "I know what you are about to ask, " + name + ".\n" +
                "I know the look on your face.\n" +
                "I would rather you said it than let it sit between us.");

        int anger = Dialogue.choose(name,
                "Something hot and sharp rises in your chest. The awe evaporates.",
                "You MADE this world. You could have stopped this. WHY DIDN'T YOU?",
                "My family prayed to you. Every day. And you just... watched them get taken?",
                "If you are the Creator - then this is YOUR fault. All of it.",
                "You have all that power and you did NOTHING?");

        switch (anger) {
            case 1 -> {
                Storyteller.tell("Zyraeth does not flinch.\n" +
                        "It absorbs the words the way stone absorbs rain - fully, without resistance.");
                Dialogue.say("Zyraeth", "Yes. That is the question.\n\n" +
                        "And you deserve a real answer, not a careful one.\n" +
                        "So let me give you one.");
            }
            case 2 -> {
                Storyteller.tell("Your voice breaks on the last word.\n" +
                        "Zyraeth is quiet for a long moment.\n" +
                        "When it speaks, something in its voice is different - heavier.");
                Dialogue.say("Zyraeth", "I heard every prayer, " + name + ".\n" +
                        "Every single one.\n\n" +
                        "Do not think for a moment that I did not.");
            }
            case 3 -> {
                Storyteller.tell("The words come out harder than you meant them to.\n" +
                        "Zyraeth's expression does not change - but something behind its eyes does.");
                Dialogue.say("Zyraeth", "You are right to be angry.\n" +
                        "Anger is the honest response.\n" +
                        "I will not ask you to set it aside. Only to listen alongside it.");
            }
            case 4 -> {
                Storyteller.tell("You are shaking. Not from fear.\n" +
                        "Zyraeth looks at you steadily.\n" +
                        "It has the expression of someone who has been waiting for exactly this.");
                Dialogue.say("Zyraeth", "Nothing.\n\n" +
                        "That word is going to be with me for a long time.\n" +
                        "You are not wrong to use it. Let me explain why it is also not the full truth.");
            }
        }

        Storyteller.cutscene(
                "Zyraeth sits - not on anything, simply folds downward until it is at eye level with you.\n" +
                "The gesture is so unexpectedly human that it stops you mid-breath.\n\n" +
                "For a moment it looks less like a god and more like someone who is very, very tired."
        );

        Dialogue.say("Zyraeth",
                "When I made this world, I made it with rules.\n\n" +
                "Not laws of kingdoms - laws of existence. Gravity. Time. Cause and consequence.\n" +
                "The rule that fire burns and water flows downward.\n" +
                "And the rule that a creator does not override the will of what they have created.\n\n" +
                "If I intervened every time something went wrong - every drought, every cruelty,\n" +
                "every moment a person chose darkness - this world would not be yours.\n" +
                "It would be a garden I tend alone. Beautiful. Empty. Meaningless.\n\n" +
                "The same rules that let your mother choose to love. That let Miri laugh.\n" +
                "That let you become who you are - those same rules forbid me from reaching down\n" +
                "and stopping the hand that was raised against them.");

        int creatorRespond = Dialogue.choose(name,
                "You listen. The anger does not leave - but something else moves in beside it.",
                "That is not good enough. They are my FAMILY.",
                "So you are powerful enough to make a world but not to save two people?",
                "...Did it hurt? Watching and not being able to stop it?",
                "Then what good are you? What can you actually do?");

        switch (creatorRespond) {
            case 1 -> {
                Dialogue.say("Zyraeth",
                        "I know.\n\n" +
                        "Every god who ever made something and watched it suffer has heard those words\n" +
                        "from themselves, in the dark, when no one was listening.\n" +
                        "They are true. They are fair. And they do not change the rules.\n\n" +
                        "What changes the rules - the one thing that can - is you.");
            }
            case 2 -> {
                Dialogue.say("Zyraeth",
                        "Yes.\n\n" +
                        "I am powerful enough to unmake a mountain. To stop a river at its source.\n" +
                        "To rewrite the stars if I chose to.\n\n" +
                        "And I cannot touch Vaelthor without destroying everything the law protects\n" +
                        "in the act of breaking it. Power without restraint is not strength, " + name + ".\n" +
                        "It is the very thing you are about to walk into battle against.");
            }
            case 3 -> {
                Storyteller.tell("Zyraeth pauses.\n\n" +
                        "A long pause. The kind that fills itself with something unspoken.");
                Dialogue.say("Zyraeth",
                        "Yes.\n\n" +
                        "I have watched suffering I could not prevent for longer than your world has existed.\n" +
                        "I have watched good people choose wrong and wrong people choose good\n" +
                        "and I have felt every moment of it.\n\n" +
                        "Hurt is perhaps too small a word.\n" +
                        "But it is the closest one.");
            }
            case 4 -> {
                Dialogue.say("Zyraeth",
                        "A fair question. Here is the answer:\n\n" +
                        "I can see what you cannot. I can find the one person capable of doing\n" +
                        "what I cannot - and I can stand them in front of the problem.\n\n" +
                        "I can give you truth when you need it, and light when the path goes dark.\n" +
                        "I can watch over your family for as long as I am able.\n" +
                        "And I can tell you, with complete certainty, that you are going to win.\n\n" +
                        "Not because I have arranged it. Because I know you.");
            }
        }

        Dialogue.say("Zyraeth",
                "I am not asking you to forgive me, " + name + ".\n" +
                "I am asking you to move.\n\n" +
                "Your grief is yours. Your anger is yours. Carry both if you need to.\n" +
                "But somewhere past the burning, your family is running out of time.\n\n" +
                "And you are the only thing in this world - or any other - that can reach them.");

        Storyteller.tell("You look at the creator of the world.\n\n" +
                "You are still angry. You think you will be angry for a long time.\n\n" +
                "But the village is burning.\n" +
                "And Miri is somewhere beyond the smoke.\n\n" +
                "You turn back to Zyraeth.");

        Storyteller.cutscene(
                "Zyraeth raises a hand. The smoke parts. A vision forms in the air before you.\n\n" +
                "You see it as if you were there - Elara, your mother, standing in the doorway of your home.\n" +
                "She had seen the sky crack open. She had tried to shield Miri with her own body.\n\n" +
                "Then came the shadows - creatures born of a broken god's grief.\n" +
                "They did not kill. They took. They carried your family into the wound in the sky\n" +
                "and sealed it behind them like a scar.\n\n" +
                "The vision fades. Your hands are shaking."
        );

        Dialogue.say("Zyraeth",
                "His name is Vaelthor. Once, he was my kin - a god of balance and memory.\n" +
                "But grief corrupted him. When his world was lost, he chose to unmake all worlds\n" +
                "rather than endure the pain of loss alone.\n\n" +
                "Your family was taken as anchors - living souls tethered to his altar\n" +
                "to fuel the ritual that will unravel this world.\n" +
                "They are alive, " + name + ". But with every moment that passes, his hold grows stronger.");

        int respond = Dialogue.choose(name,
                "You take in every word. Your grief hardens into something sharper.",
                "Then I will tear his altar apart with my bare hands.",
                "Why didn't you stop him yourself?",
                "How long do they have?",
                "Is there anyone else who knows about this?",
                "Tell me everything. I won't waste a single step.");

        switch (respond) {
            case 1 -> {
                Dialogue.say("Zyraeth", "Good. Rage is a fire. Let it guide you - but do not let it consume you.\n" +
                        "You will need a clear mind when you face him.");
            }
            case 2 -> {
                Dialogue.say("Zyraeth", "I tried. My light alone was not enough to reach him.\n" +
                        "A god consumed by darkness cannot be undone by another god - only by mortal will.\n" +
                        "That is why it must be you.");
            }
            case 3 -> {
                Dialogue.say("Zyraeth", "Less time than I would like. The ritual completes when the last star\n" +
                        "above Aethelgard goes dark. You have until then.\n" +
                        "Look up - one already flickers.");
            }
            case 4 -> {
                Dialogue.say("Zyraeth", "There were others who knew. Most fell to Vaelthor's corrupted servants.\n" +
                        "The few that remain are scattered - but the path through them leads to the altar.");
            }
            case 5 -> {
                Dialogue.say("Zyraeth", "Wisdom and fire - a rare combination.\n" +
                        "Then listen closely, " + name + ". Every enemy between you and Vaelthor was\n" +
                        "once something else. Something whole. Remember that - it may matter before the end.");
            }
        }

        Dialogue.say("Zyraeth", "Your grief echoes through creation, " + name + ". But grief alone will not save them.");

        int vow = Dialogue.choose(name,
                "You stand. Whatever you were before this moment - you are something else now.",
                "I will save them. No matter the cost.",
                "Why me? I am no hero.",
                "Guide me. I don't know where to start.",
                "I'm not doing this for glory. I'm doing this for them.",
                "If Vaelthor wants to unmake the world - he'll have to go through me first.");

        switch (vow) {
            case 1 -> {
                Dialogue.say("Zyraeth", "Then the cost will be great. But I believe you can bear it.");
            }
            case 2 -> {
                Dialogue.say("Zyraeth", "No hero ever knew they were one before the moment demanded it.\n" +
                        "You are exactly who this moment needs - someone with everything to lose.");
            }
            case 3 -> {
                Dialogue.say("Zyraeth", "Then I will be your compass. Start with the village.\n" +
                        "His servants are already here - consuming what they could not carry.\n" +
                        "Fight through them. The altar lies beyond the Great Forest.");
            }
            case 4 -> {
                Dialogue.say("Zyraeth", "That love - that ordinary, stubborn love - is the one thing\n" +
                        "Vaelthor no longer possesses. It will be your greatest weapon.");
            }
            case 5 -> {
                Dialogue.say("Zyraeth", "Then stand firm, " + name + ".\n" +
                        "I will lend you what light I can. The rest... is yours to forge.");
            }
        }

        Storyteller.tell("Zyraeth places a hand on your chest.\nYou feel something ignite - not fire, but purpose.\n\n" +
                "The radiant being begins to fade back into the light.\n\n" +
                "\"I will watch over them as long as I am able,\" she says.\n" +
                "\"Now go, " + name + ". Bring them home.\"");

        Storyteller.tell("Zyraeth's light sweeps across the ruined square.\n" +
                "Four weapons emerge from the glow, hovering before you.\n\n" +
                "\"Choose,\" she says. \"What you reach for will tell me who you are.\"");

        int weaponChoice = Dialogue.choose(name,
                "Four weapons float before you, each humming with a different energy.\nWhich do you take?",
                "A massive Greatsword - its blade etched with ancient runes.       [ Warrior ]",
                "An Elven Recurve Bow - light as a whisper, deadly as silence.     [ Archer  ]",
                "A Staff of the Four Winds - humming with fire, ice, and lightning. [ Mage    ]",
                "Twin Shadow Daggers - so dark they seem to swallow the light.     [ Rogue   ]");

        Character player = switch (weaponChoice) {
            case 1 -> new Warrior(name);
            case 2 -> new Archer(name);
            case 3 -> new Mage(name);
            case 4 -> new Rogue(name);
            default -> new Warrior(name);
        };

        switch (weaponChoice) {
            case 1 -> {
                Storyteller.tell("Your hand closes around the hilt of the Greatsword of Aethelheart.\n" +
                        "It is heavier than you expected - and yet it feels like an extension of your arm.");
                Dialogue.say("Zyraeth",
                        "A Warrior's heart. You face what frightens you head-on.\n" +
                        "Good. What stands between you and your family will not yield to hesitation.");
            }
            case 2 -> {
                Storyteller.tell("Your fingers find the grip of the Elven Recurve Bow.\n" +
                        "The string hums as you draw it back - responsive, alive, precise.");
                Dialogue.say("Zyraeth",
                        "An Archer's eye. You see farther than others dare to look.\n" +
                        "Mind your arrows. Each one is a decision - make them count.");
            }
            case 3 -> {
                Storyteller.tell("The Staff of the Four Winds leaps into your hand.\n" +
                        "Fire warms your palm. Ice chills your fingertips. Lightning hums in the grain of the wood.\n" +
                        "The elements know you already. They have been waiting.");
                Dialogue.say("Zyraeth",
                        "An Elemental Mage. You do not bend one force - you speak to all of them.\n" +
                        "Fire for fury. Ice for patience. Lightning for the moment between.\n" +
                        "Guard your mana. The elements answer only when you have the will to call them.");
            }
            case 4 -> {
                Storyteller.tell("The Twin Shadow Daggers practically leap into your hands.\n" +
                        "They are lighter than thought - and colder than the night air.");
                Dialogue.say("Zyraeth",
                        "A Rogue's instinct. You know that the unseen blow lands deepest.\n" +
                        "Use the shadows. They will be kinder to you than the open field.");
            }
        }

        System.out.println("\n  Weapon chosen - you walk the path of the " + player.job + ".");
        System.out.println("  ---------------------------------------------------------");
        printStats(player);

        /* ===========================================================
           ENCOUNTER 1: Corrupted Villager
           =========================================================== */
        Storyteller.tell(
                "The first thing you notice is the silence between the screams.\n\n" +
                "Your village - Aethelgard, the place you have known every stone of since childhood -\n" +
                "is burning.\n\n" +
                "And something is moving through the smoke.\n\n" +
                "Not running. Not fleeing.\n" +
                "Moving toward you.\n\n" +
                "You recognize the face. You wish you did not.\n" +
                "It is someone you have known your whole life - a neighbor, a familiar presence\n" +
                "at every harvest and every celebration.\n\n" +
                "But the eyes are wrong. The way it moves is wrong.\n" +
                "Whatever it is now - it is not the person you remember.\n\n" +
                "It screams. And charges.");
        Enemy villager = new Enemy("Corrupted Villager", 120, 12, 50);
        villager.setAttackLines(
                "lunges wildly with twisted limbs!",
                "lets out a broken scream and strikes!",
                "thrashes in agony!"
        );
        if (!battle(player, villager)) return askRestart();
        recoverAfterBattle(player);
        unlockNextSkill(player);

        Storyteller.tell(
                "It is over.\n\n" +
                "The corrupted villager crumples to the ground. The dark energy that animated it\n" +
                "dissipates like smoke in wind - and for one terrible moment, you see their face\n" +
                "return to something like peace.\n\n" +
                "You do not have time to grieve. The village is still burning.\n" +
                "Your family is still gone.\n\n" +
                "You move deeper into the smoke.");

        /* ===========================================================
           ENCOUNTER 2: Forest Wraith
           =========================================================== */
        Storyteller.tell(
                "Beyond the burning homes, the smoke thickens into something unnatural.\n\n" +
                "It does not move like smoke should. It coils. It reaches.\n\n" +
                "The temperature drops so suddenly your breath becomes fog.\n" +
                "The fires around you dim - as though something nearby is drinking their warmth.\n\n" +
                "You hear it before you see it.\n\n" +
                "A sound like every sad thing you have ever felt, given a voice.\n\n" +
                "The Forest Wraith drifts out of the haze - a silhouette made of absence,\n" +
                "trailing grief like a cloak. It is drawn here by the suffering in the air.\n" +
                "By your suffering.\n\n" +
                "It turns toward you. And it is hungry.");
        Enemy wraith = new Enemy("Forest Wraith", 140, 16, 55);
        wraith.setAttackLines(
                "passes through you with a chilling touch!",
                "wails and lashes out with spectral claws!",
                "drains your warmth with an eerie shriek!"
        );
        if (!battle(player, wraith)) return askRestart();
        recoverAfterBattle(player);

        Storyteller.tell(
                "The wraith dissolves.\n\n" +
                "Not with a crash or a cry - it simply comes apart,\n" +
                "like a bad dream remembered in daylight.\n\n" +
                "The cold lifts. The smoke thins. Somewhere ahead you can hear\n" +
                "the sound of the Great Forest - restless, alive, wrong.\n\n" +
                "The path forward cuts through it.\n" +
                "You follow the edge of the firelight as far as it reaches.\n" +
                "Then you step into the dark.");

        /* ===========================================================
           ENCOUNTER 3: Goblin Chieftain
           =========================================================== */
        Storyteller.tell(
                "You smell the camp before you see it.\n\n" +
                "Goblins - a raiding party, drawn by the chaos - have made a nest\n" +
                "at the edge of the forest from the wreckage of what they have stolen.\n\n" +
                "And there, in the firelight, you see it.\n\n" +
                "A flash of silver at the belt of the largest figure.\n\n" +
                "Miri's locket. The one your mother clasped around her neck\n" +
                "on the morning everything was still ordinary.\n\n" +
                "The Goblin Chieftain sees you at the same moment you see him.\n" +
                "He grins - all teeth and malice - and wraps one thick fist around the locket.\n\n" +
                "\"Come and take it,\" he snarls.");
        Enemy goblin = new Enemy("Goblin Chieftain", 180, 18, 65);
        goblin.setAttackLines(
                "cackles and slashes!",
                "lunges with savage fury!",
                "swings wildly while clutching the locket!"
        );
        if (!battle(player, goblin)) return askRestart();
        recoverAfterBattle(player);
        unlockNextSkill(player);

        Storyteller.tell(
                "The chieftain falls.\n\n" +
                "The rest of the goblins scatter into the underbrush - cowards without their leader.\n\n" +
                "You kneel and pry the locket from his fingers.\n" +
                "The silver is warm. You close your hand around it.\n\n" +
                "Miri was wearing this this morning.\n" +
                "She was alive this morning.\n\n" +
                "She is still alive. She has to be.\n\n" +
                "You stand. You keep moving.\n" +
                "The altar is close - you can feel it the way you feel a storm before the rain arrives.");

        /* ===========================================================
           ENCOUNTER 4: Stone Colossus
           =========================================================== */
        Storyteller.tell(
                "You find the Ancient Altar at the heart of the forest.\n\n" +
                "Or rather - you find what is guarding it.\n\n" +
                "The ground shakes before you see it. Then a shadow falls across the trees -\n" +
                "far larger than any shadow should be.\n\n" +
                "The Stone Colossus is old. Older than Aethelgard. Older than the forest around it.\n" +
                "It was carved to protect something - the altar, the old magic,\n" +
                "something that mattered to whoever built it.\n\n" +
                "It does not know that what it guards has been corrupted.\n" +
                "It only knows its purpose: nothing passes.\n\n" +
                "It raises one massive fist.\n" +
                "The earth cracks beneath the blow even before it lands.\n\n" +
                "You have nowhere to run.");
        Enemy colossus = new Enemy("Stone Colossus", 260, 28, 80);
        colossus.setAttackLines(
                "slams the ground with a thunderous fist!",
                "sweeps you with a massive stone arm!",
                "rises to full height and crashes down!"
        );
        if (!battle(player, colossus)) return askRestart();

        recoverAfterBattle(player);
        Storyteller.tell(
                "The Colossus sways.\n\n" +
                "Cracks spider-web across its chest, its arms, the ancient face that was never\n" +
                "meant to hold any expression. It makes no sound as it falls.\n\n" +
                "Just a long, slow collapse - like a cliff giving way to the sea.\n\n" +
                "The rubble settles. Dust rises in a cloud that swallows the moonlight.\n\n" +
                "When it clears, the altar steps are visible ahead.\n\n" +
                "You are bleeding. You are exhausted.\n" +
                "You take one breath. Then another.\n" +
                "Then you climb.");

        /* ===========================================================
           ENCOUNTER 5: Fallen Paladin
           =========================================================== */
        Storyteller.tell(
                "At the top of the altar steps, someone is waiting for you.\n\n" +
                "Not a monster. Not a shadow-creature.\n" +
                "A person - standing straight, wearing armor that was once holy.\n" +
                "You can still see the crest of Aethelgard's old order etched into the breastplate,\n" +
                "beneath the dark stains that have soaked into every seam.\n\n" +
                "The Fallen Paladin was a champion once.\n" +
                "You can see it in the way they stand - trained, controlled, deliberate.\n" +
                "Someone who spent years serving the light.\n\n" +
                "And then - something broke.\n\n" +
                "They look at you with eyes that are almost clear.\n" +
                "Almost themselves.\n\n" +
                "\"Turn back,\" they say. Their voice is rough, like speaking through pain.\n" +
                "\"You cannot win. And if you somehow do... what comes after is worse.\"\n\n" +
                "They raise the corrupted blade anyway.\n" +
                "They have no choice. Neither do you.");
        Enemy paladin = new Enemy("Fallen Paladin", 320, 35, 90);
        paladin.setAttackLines(
                "swings a corrupted holy blade with terrible force!",
                "calls upon dark blessings and strikes!",
                "cries out in anguish and hammers down!"
        );
        if (!battle(player, paladin)) return askRestart();
        recoverAfterBattle(player);
        unlockNextSkill(player);

        Storyteller.tell(
                "The Fallen Paladin drops to one knee.\n\n" +
                "The dark energy leaves them all at once - you can see it go,\n" +
                "like ink bleeding out of water.\n" +
                "What remains is a person who is exhausted and old and has been fighting\n" +
                "something much harder than you for much longer.\n\n" +
                "They look up at you. Something in their eyes is grateful.\n\n" +
                "\"Vaelthor is beyond that door,\" they say quietly.\n" +
                "\"He is in pain. Remember that, when you face him.\n" +
                "It will not make him less dangerous.\n" +
                "But it might make you more merciful.\n" +
                "And mercy... is the one thing he cannot counter.\"\n\n" +
                "They close their eyes.\n\n" +
                "You face the altar door.");

        if (player.ultimate != null) {
            Storyteller.cutscene(
                "And then you feel it.\n\n" +
                "Something deep inside you - deeper than bone, deeper than memory -\n" +
                "cracks open like an egg held too long in a clenched fist.\n\n" +
                "The energy you have been building through every fight,\n" +
                "every wound, every desperate swing - it is still inside you.\n" +
                "Not lost. Waiting.\n\n" +
                "You understand now what it is.\n\n" +
                "Ultimate unlocked: " + player.ultimate.getName() + "\n" +
                "  >> " + player.ultimate.getDescription() + "\n\n" +
                "Charge your ultimate by landing attacks in battle.\n" +
                "When the gauge reaches 100 -- it is ready."
            );
        }

        /* ------ FINAL BOSS DIALOGUE ------ */
        Storyteller.cutscene(
                "The altar trembles.\n\n" +
                "At its center, a figure - neither shadow nor light, but the space between them.\n" +
                "His eyes hold the remnants of stars he has already extinguished.\n\n" +
                "'Zyraeth...'\n\n" +
                "'Brother...'\n\n" +
                "Vaelthor, the Shattered Divinity, turns to face you.\n" +
                "And for one moment - just one - he looks almost sorrowful."
        );

        Dialogue.say("Vaelthor",
                "So. She sent a mortal.\n" +
                "How like her - to believe that something fragile\n" +
                "can accomplish what eternity could not.");

        Dialogue.say("Zyraeth",
                "Vaelthor. Release them. This is not what you were.");

        Dialogue.say("Vaelthor",
                "What I was.\n" +
                "What I was is ash, Zyraeth. What I was died with everything I protected.\n" +
                "This world deserves the same ending mine received.\n" +
                "Call it justice. Call it symmetry. I call it mercy.");

        int d1 = Dialogue.choose(name,
                "You step forward. Your voice does not shake.",
                "Let them go. This has nothing to do with them.",
                "You lost something. I understand that. But this isn't the answer.",
                "You're not merciful. You're just afraid to grieve alone.",
                "They are my family. And I am taking them back.",
                "You want someone to blame for your pain. I won't be it.");

        switch (d1) {
            case 1 -> Dialogue.say("Vaelthor",
                "Nothing to do with them?\n" +
                "Every life in this world is a thread in the tapestry I mean to unravel.\n" +
                "They are not targets, mortal. They are simply... included."
            );
            case 2 -> Dialogue.say("Vaelthor",
                "Understand?\n" +
                "You have endured one evening of loss.\n" +
                "I have carried mine across ten thousand years.\n" +
                "Do not speak to me of understanding."
            );
            case 3 -> Dialogue.say("Vaelthor",
                "...\n" +
                "Afraid.\n" +
                "I have not heard that word directed at me in centuries.\n" +
                "Perhaps you are more perceptive than most. That will make this harder for you."
            );
            case 4 -> Dialogue.say("Vaelthor",
                "Taking them back.\n" +
                "From a god.\n" +
                "The audacity of mortals - it is the one thing about your kind I once admired.\n" +
                "Now it is simply an obstacle."
            );
            case 5 -> Dialogue.say("Vaelthor",
                "Blame?\n" +
                "I blame no one. That is what makes me more dangerous than you know.\n" +
                "Revenge can be reasoned with. Despair cannot."
            );
        }

        int d2 = Dialogue.choose("Vaelthor",
                "Answer me, mortal. Why do you resist the inevitable end?",
                "Because even if it's fleeting... it matters.",
                "I fight for those I love.",
                "If the world is broken... then I'll be the one to fix it.",
                "Because giving up is the one thing I refuse to do.",
                "Because my family is still in there, and I am not leaving without them.",
                "Because you were once something worth saving too - and someone should have fought for you.");

        switch (d2) {
            case 1 -> Dialogue.say("Vaelthor",
                "Meaning... is a lie created to comfort the weak.\n" +
                "But if you believe in it so deeply... show me its worth."
            );
            case 2 -> Dialogue.say("Vaelthor",
                "Attachment.\nThe chain that binds all mortals to suffering.\n" +
                "Then struggle. Suffer. Prove that your bonds are not meaningless."
            );
            case 3 -> Dialogue.say("Vaelthor",
                "To repair what is flawed... you must first surpass its creator.\n" +
                "Then rise, mortal. Show me a will that rivals the divine."
            );
            case 4 -> Dialogue.say("Vaelthor",
                "Refusal.\n" +
                "The mortal's oldest weapon - and their most foolish.\n" +
                "Very well. Let your stubbornness be your eulogy."
            );
            case 5 -> {
                Dialogue.say("Vaelthor",
                    "They are here because they were close to you.\n" +
                    "Come then. Take them. The altar will not surrender them without a fight -\n" +
                    "and neither will I."
                );
            }
            case 6 -> {
                Dialogue.say("Vaelthor",
                    "...\n\n" +
                    "That is... not what I expected you to say."
                );
                Storyteller.cutscene(
                    "For a moment - a fraction of a breath - something shifts behind Vaelthor's eyes.\n" +
                    "Not weakness. Not mercy.\n\n" +
                    "Something older. Something that remembers what it felt like to be whole.\n\n" +
                    "Then it closes again, like a wound that cannot heal.\n\n" +
                    "'...No one fought for me,' he says quietly.\n" +
                    "'And here we are.'"
                );
            }
        }

        Dialogue.say("Zyraeth", "Vaelthor... this world is not yours to erase.\n" +
                "You were made to protect living things - not to decide which ones deserve to end.");
        Dialogue.say("Vaelthor", "And you were made to guide them.\n" +
                "Tell me, Zyraeth - how many have you guided into graves?");
        Dialogue.say("Zyraeth", "Enough. I will not trade grief for grief with you.\n" +
                "Then let this mortal prove what even a god could not.");
        Dialogue.say("Vaelthor", "A mortal.\n" +
                "Against a god who has nothing left to lose.\n\n" +
                "You are either very brave or very foolish.\n\n" +
                "...I find I no longer know the difference.\n\n" +
                "Very well. Let the final judgment begin.\n" +
                "Show me - show me that this broken world is worth preserving.\n" +
                "Because I have forgotten. And I desperately need to be reminded.");

        /* ------ ENCOUNTER 6 (BOSS): Vaelthor - with Zyraeth's Revival ------ */
        Enemy god = new Enemy("Vaelthor, the Shattered Divinity", 650, 45, 150);
        god.setAttackLines(
                "warps reality itself!",
                "crushes you with divine force!",
                "tears space apart!"
        );

        boolean bossResult = bossBattle(player, god, name);
        if (!bossResult) return askRestart();

        System.out.println("\n--- VICTORY ---");
        System.out.println("The Shattered Divinity falls.");

        Storyteller.cutscene(
                "Vaelthor staggers.\n\n" +
                "The altar fractures - a web of cracks splitting across its ancient stone,\n" +
                "light bleeding through every line like sunrise through broken glass.\n\n" +
                "He looks at his hands. Then at you.\n\n" +
                "And for the first time in ten thousand years - he is not a god.\n" +
                "He is simply someone who is tired."
        );

        Dialogue.say("Vaelthor",
                "You reminded me...\n\n" +
                "I had forgotten what it felt like to lose something worth fighting for.\n\n" +
                "I think... I needed to be shown that such things still exist.\n\n" +
                "...Forgive me. For all of it.");

        Dialogue.say("Zyraeth",
                "It is not mine to give, brother.\n" +
                "But perhaps the world you tried to end... will remember you differently\n" +
                "because of what happened here.");

        Storyteller.cutscene(
                "The altar collapses inward - and from the light that pours out,\n" +
                "three figures emerge.\n\n" +
                "Elara. Miri. Stumbling, blinking, alive.\n\n" +
                "Miri looks around at the crumbling stone and the fading light.\n" +
                "Then she sees you.\n\n" +
                "She runs.\n\n" +
                "\"You came,\" she whispers into your shoulder.\n" +
                "\"I knew you would come.\"\n\n" +
                "Your mother says nothing. She simply takes your face in both hands\n" +
                "and looks at you - the way she looked at you when you were small\n" +
                "and she wanted to memorize every part of you.\n\n" +
                "Zyraeth watches from a distance, already beginning to fade.\n\n" +
                "\"Well done, " + name + ",\" she says softly.\n" +
                "\"Well done.\""
        );

        Storyteller.tell(
                "--- Aethelgard endures. The family is home. ---\n\n" +
                "    And the legend of " + name + " begins."
        );

        return askRestart();
    }

    // ================================================================
    //  BATTLE HELPERS
    // ================================================================

    /** Reads a battle action choice safely through the shared reader. */
    private static int readBattleChoice(int totalOptions) {
        int choice = -1;
        while (choice < 1 || choice > totalOptions) {
            System.out.print("Choose (1-" + totalOptions + "): ");
            try {
                String raw = reader.readLine();
                if (raw == null) raw = "1";
                choice = Integer.parseInt(raw.trim());
                if (choice < 1 || choice > totalOptions)
                    System.out.println("  [Invalid. Enter a number between 1 and " + totalOptions + ".]");
            } catch (NumberFormatException | IOException ex) {
                System.out.println("  [Please enter a number.]");
            }
        }
        return choice;
    }

    /** Prints the battle action menu and returns the number of valid options. */
    private static int printBattleMenu(Character p) {
        int ultimateSlot = (p.energy >= 100 && p.ultimate != null) ? 3 + p.activeMoves.size() : -1;
        int totalOptions = 2 + p.activeMoves.size() + (ultimateSlot != -1 ? 1 : 0);

        System.out.println("\nWhat will you do?");
        System.out.println("  1. Light Attack  (costs " + Character.LIGHT_COST + " stamina)");
        System.out.println("  2. Heavy Attack  (costs " + Character.HEAVY_COST + " stamina)");
        if (p.activeMoves.isEmpty()) {
            System.out.println("  -- Skills: none yet -- defeat enemies to earn skills --");
        } else {
            System.out.println("  -- Skills --------------------------------------------------");
            for (int i = 0; i < p.activeMoves.size(); i++) {
                SpecialMove sm = p.activeMoves.get(i);
                System.out.println("  " + (i + 3) + ". [" + sm.getName() + "] -- " + sm.getDescription());
            }
        }
        if (ultimateSlot != -1) {
            System.out.println("  " + ultimateSlot + ". [ULTIMATE: " + p.ultimate.getName() + "] -- " + p.ultimate.getDescription());
        }
        System.out.println("  ------------------------------------------------------------");
        return totalOptions;
    }

    /** Executes the chosen action for the player against the enemy. */
    private static void executePlayerAction(Character p, Enemy e, int choice) {
        int ultimateSlot = (p.energy >= 100 && p.ultimate != null) ? 3 + p.activeMoves.size() : -1;

        if (choice == 1) {
            p.lightAttack(e);
            p.gainEnergy();
        } else if (choice == 2) {
            p.heavyAttack(e);
            p.gainEnergy();
        } else if (ultimateSlot != -1 && choice == ultimateSlot) {
            Storyteller.action(p.getName() + " " + p.ultimate.getDescription());
            p.ultimate.use(p, e);
            p.energy = 0;
        } else {
            int idx = choice - 3;
            if (idx >= 0 && idx < p.activeMoves.size()) {
                SpecialMove m = p.activeMoves.get(idx);
                Storyteller.action(p.getName() + " " + m.getDescription());
                m.use(p, e);
                p.gainEnergy();
            } else {
                System.out.println("  [Invalid choice - defaulting to light attack]");
                p.lightAttack(e);
                p.gainEnergy();
            }
        }
    }

    static boolean bossBattle(Character p, Enemy e, String playerName) {
        boolean zyraethRevivalUsed = false;

        System.out.println("\n--- BATTLE START ---");
        System.out.println("vs. " + e.getName());
        System.out.println("--------------------");

        while (p.getHealth() > 0 && e.getHealth() > 0) {

            System.out.println("\n[ YOUR STATUS ]");
            System.out.println("  HP: " + p.getHealth() + "  |  Stamina: " + p.getStamina() + extraResource(p));
            System.out.println("  Ultimate Charge: " + p.energy + " / 100" + (p.energy >= 100 ? "  <<< READY >>>" : ""));
            System.out.println("[ ENEMY ]");
            System.out.println("  " + e.getName() + "  HP: " + e.getHealth() + "  Stamina: " + e.getStamina());
            System.out.println("----------------------------------------");

            int totalOptions = printBattleMenu(p);
            int choice = readBattleChoice(totalOptions);

            int enemyHpBefore = e.getHealth();
            int playerHpBefore = p.getHealth();
            executePlayerAction(p, e, choice);
            int damageDealtToEnemy = enemyHpBefore - e.getHealth();
            if (damageDealtToEnemy > 0) {
                System.out.println("  >> You dealt " + damageDealtToEnemy + " damage to "
                        + e.getName() + "! (Enemy HP: " + enemyHpBefore + " -> " + Math.max(e.getHealth(), 0) + ")");
            }

            if (e.getHealth() > 0) {
                e.takeTurn(p);
                int damageDealtToPlayer = playerHpBefore - p.getHealth();
                if (damageDealtToPlayer > 0) {
                    System.out.println("  >> " + e.getName() + " dealt " + damageDealtToPlayer
                            + " damage to you! (Your HP: " + playerHpBefore + " -> " + Math.max(p.getHealth(), 0) + ")");
                }
            }

            p.regenStamina();
            e.regenStamina();
            System.out.println("  [End of turn: both sides recover 5 stamina]");

            if (p.getHealth() <= 0) {
                if (!zyraethRevivalUsed) {
                    zyraethRevivalUsed = true;
                    zyraethRevival(p, e, playerName);
                    // p.health is now > 0 - loop continues
                } else {
                    Storyteller.tell("--- GAME OVER ---\n\n" +
                            playerName + " has fallen... even Zyraeth's light could not reach you twice.");
                    return false;
                }
            }
        }

        if (p.getHealth() <= 0) {
            Storyteller.tell("--- GAME OVER ---\n\n" +
                    playerName + " has fallen...");
            return false;
        }

        System.out.println("\n  " + e.getName() + " has been defeated!");
        return true;
    }

    static void zyraethRevival(Character p, Enemy vaelthor, String playerName) {
        int maxHp = p instanceof Warrior ? 400
                  : p instanceof Archer  ? 300
                  : p instanceof Mage    ? 240
                  : 260;

        int reviveHp = (int)(maxHp * 0.45);
        p.health = reviveHp;

        int staminaMax = p instanceof Warrior ? 120
                       : p instanceof Archer  ? 100
                       : p instanceof Mage    ? 80
                       : 130;
        p.stamina = staminaMax;

        if (p instanceof Mage mage) {
            mage.restoreMana((int)(mage.getMaxMana() * 0.50));
        } else if (p instanceof Archer archer) {
            archer.restoreArrows(10);
        }

        // Zyraeth's divine strike staggers Vaelthor but deliberately cannot finish him -
        // the player must land the killing blow themselves.
        int divineStrike = 80;
        int strikeActual = Math.min(divineStrike, vaelthor.getHealth() - 1);
        if (strikeActual > 0) {
            vaelthor.takeDamage(strikeActual);
        }
        // vaelthor.health is now guaranteed >= 1 regardless of the strike amount.

        Storyteller.cutscene(
            "You fall.\n\n" +
            "The altar stone rushes up to meet you. The world tilts sideways.\n" +
            "Vaelthor looms above - vast, dark, indifferent.\n\n" +
            "And then - everything goes white.\n\n" +
            "Not the darkness of ending. Something else entirely.\n\n" +
            "A warmth that does not burn. A sound that is not quite sound -\n" +
            "more like the feeling of a name spoken by someone who loves you.\n\n" +
            "You feel a hand on your chest.\n\n" +
            "Not heavy. Barely there at all.\n" +
            "But unmistakable."
        );

        Dialogue.say("Zyraeth",
            "Not yet.\n\n" +
            "Do you hear me, " + playerName + "?\n" +
            "Not yet.\n\n" +
            "I told you I would watch over them as long as I am able.\n" +
            "I did not say I would only watch over them.\n\n" +
            "I am breaking my own rules for you.\n" +
            "I have never done that before.\n" +
            "Do not make me regret it."
        );

        Storyteller.cutscene(
            "The light pours into you - not all of it, not even most of it.\n" +
            "Zyraeth cannot give you what you are not strong enough to hold.\n\n" +
            "But enough.\n\n" +
            "Enough to breathe. Enough to rise.\n" +
            "Enough to remember why you are here.\n\n" +
            "The light does not stop at you.\n" +
            "A thread of it - thin, golden, quietly furious - strikes Vaelthor\n" +
            "like a word spoken that cannot be taken back.\n\n" +
            "He staggers. Not much. But enough."
        );

        Dialogue.say("Vaelthor",
            "Zyraeth...\n\n" +
            "You are breaking the law.\n" +
            "You made that law. YOU.\n\n" +
            "What are they to you that you would unmake yourself for them?"
        );

        Dialogue.say("Zyraeth",
            "Everything.\n\n" +
            "Every ordinary, stubborn, grieving, loving, impossible mortal\n" +
            "is everything to me, Vaelthor.\n" +
            "That is what you forgot.\n" +
            "That is what broke you.\n\n" +
            "I cannot fight you.\n" +
            "But " + playerName + " can.\n" +
            "And I will spend whatever I have left to make sure they do."
        );

        Storyteller.cutscene(
            "The light fades.\n\n" +
            "Not completely - a faint warmth still lingers in your chest,\n" +
            "like an ember someone cupped in their hands to keep it from going out.\n\n" +
            "You are not whole. You are not healed.\n" +
            "You are breathing.\n\n" +
            "You are on your feet.\n\n" +
            "And Vaelthor - for the first time since this fight began -\n" +
            "looks at you with something other than contempt.\n\n" +
            "He looks at you like a question he does not know the answer to.\n\n" +
            "[Zyraeth has revived you to " + reviveHp + " HP!]\n" +
            "[Her light struck Vaelthor - but she left him standing. The final blow is yours to land.]\n" +
            "[Your stamina and resources have been restored. This was Zyraeth's only intervention.]\n\n" +
            "Finish this."
        );
    }

    static boolean battle(Character p, Enemy e) {
        System.out.println("\n--- BATTLE START ---");
        System.out.println("vs. " + e.getName());
        System.out.println("--------------------");

        while (p.getHealth() > 0 && e.getHealth() > 0) {

            System.out.println("\n[ YOUR STATUS ]");
            System.out.println("  HP: " + p.getHealth() + "  |  Stamina: " + p.getStamina() + extraResource(p));
            System.out.println("  Ultimate Charge: " + p.energy + " / 100" + (p.energy >= 100 ? "  <<< READY >>>" : ""));
            System.out.println("[ ENEMY ]");
            System.out.println("  " + e.getName() + "  HP: " + e.getHealth() + "  Stamina: " + e.getStamina());
            System.out.println("----------------------------------------");

            int totalOptions = printBattleMenu(p);
            int choice = readBattleChoice(totalOptions);

            int enemyHpBefore = e.getHealth();
            int playerHpBefore = p.getHealth();
            executePlayerAction(p, e, choice);
            int damageDealtToEnemy = enemyHpBefore - e.getHealth();
            if (damageDealtToEnemy > 0) {
                System.out.println("  >> You dealt " + damageDealtToEnemy + " damage to "
                        + e.getName() + "! (Enemy HP: " + enemyHpBefore + " -> " + Math.max(e.getHealth(), 0) + ")");
            }

            if (e.getHealth() > 0) {
                e.takeTurn(p);
                int damageDealtToPlayer = playerHpBefore - p.getHealth();
                if (damageDealtToPlayer > 0) {
                    System.out.println("  >> " + e.getName() + " dealt " + damageDealtToPlayer
                            + " damage to you! (Your HP: " + playerHpBefore + " -> " + Math.max(p.getHealth(), 0) + ")");
                }
            }

            p.regenStamina();
            e.regenStamina();
            System.out.println("  [End of turn: both sides recover 5 stamina]");
        }

        if (p.getHealth() <= 0) {
            Storyteller.tell("--- GAME OVER ---\n\n" +
                    p.getName() + " has fallen...");
            return false;
        }

        System.out.println("\n  " + e.getName() + " has been defeated!");
        return true;
    }

    /* --------------------------------------------------------------------------------------------------------------------------
       SKILL UNLOCK
       -------------------------------------------------------------------------------------------------------------------------- */
    static void unlockNextSkill(Character p) {
        if (p.movePool.isEmpty()) return;

        SpecialMove gained = p.movePool.remove(0);
        p.activeMoves.add(gained);

        int skillNum = p.activeMoves.size();

        String flavour;
        if (p instanceof Warrior) {
            flavour = switch (skillNum) {
                case 1 -> "The battle-rage courses through you. Your grip on the Greatsword tightens.\n" +
                          "A technique rises from instinct - one you did not know you possessed.";
                case 2 -> "Blood and sweat and survival. The body learns what the mind never could.\n" +
                          "You feel the weapon become part of you.";
                case 3 -> "You have stood at the edge of death and stepped back.\n" +
                          "Something ancient in your blood wakes fully.";
                default -> "A new power stirs within you.";
            };
        } else if (p instanceof Archer) {
            flavour = switch (skillNum) {
                case 1 -> "Your hands move faster than thought. The bow feels lighter.\n" +
                          "You see the battlefield differently now - every angle, every opening.";
                case 2 -> "The rhythm of combat settles into you like a second heartbeat.\n" +
                          "You know where the next arrow needs to go before you draw.";
                case 3 -> "The forest taught you patience. The battlefield is teaching you precision.\n" +
                          "You are becoming something the darkness cannot easily track.";
                default -> "A new power stirs within you.";
            };
        } else if (p instanceof Mage) {
            flavour = switch (skillNum) {
                case 1 -> "The arcane current responds to your grief.\n" +
                          "It bends toward your intent with a sharpness it did not have before.";
                case 2 -> "The staff hums at a new frequency. The air smells of ozone and old starlight.\n" +
                          "The magic is not just obeying you now - it is anticipating you.";
                case 3 -> "You have reached into the deep current of creation and pulled something back.\n" +
                          "Zyraeth's light flickers at the edge of your vision - approving.";
                default -> "A new power stirs within you.";
            };
        } else {
            flavour = switch (skillNum) {
                case 1 -> "The shadows welcome you back. You slip between heartbeats\n" +
                          "and find the angle no one is watching.";
                case 2 -> "Every fight is a conversation. You have learned to speak it fluently.\n" +
                          "The daggers move before you decide to move them.";
                case 3 -> "You are no longer a person fighting in the dark.\n" +
                          "You are the dark. Everything else is just standing in it.";
                default -> "A new power stirs within you.";
            };
        }

        Storyteller.cutscene(
            flavour + "\n\n" +
            "New skill unlocked: " + gained.getName() + "\n" +
            "  >> " + gained.getDescription()
        );
    }

    /* --------------------------------------------------------------------------------------------------------------------------
       POST-BATTLE RECOVERY
       -------------------------------------------------------------------------------------------------------------------------- */
    static void recoverAfterBattle(Character p) {
        int maxHp   = p instanceof Warrior ? 400
                    : p instanceof Archer  ? 300
                    : p instanceof Mage    ? 240
                    : 260;

        int hpGain  = (int)(maxHp * 0.30);
        int hpBefore = p.health;
        p.health    = Math.min(p.health + hpGain, maxHp);
        int actualHpGain = p.health - hpBefore;

        System.out.println("\n  [ Recovery ]");
        System.out.println("  HP restored:  +" + actualHpGain + "  (now " + p.health + " / " + maxHp + ")");

        if (p instanceof Mage mage) {
            int manaGain = (int)(mage.getMaxMana() * 0.50);
            int manaBefore = mage.getMana();
            mage.restoreMana(manaGain);
            int actualManaGain = mage.getMana() - manaBefore;
            System.out.println("  Mana restored: +" + actualManaGain + "  (now " + mage.getMana() + " / " + mage.getMaxMana() + ")");
        } else if (p instanceof Archer archer) {
            int arrowsBefore = archer.getArrows();
            archer.restoreArrows(8);
            int gained = archer.getArrows() - arrowsBefore;
            System.out.println("  Arrows recovered: +" + gained + "  (now " + archer.getArrows() + ")");
        }
        int staminaMax = p instanceof Warrior ? 120 : p instanceof Archer ? 100 : p instanceof Mage ? 80 : 130;
        p.stamina = staminaMax;
        System.out.println("  Stamina fully restored.\n");
    }

    static String extraResource(Character p) {
        if (p instanceof Archer) return "  |  Arrows: " + ((Archer) p).getArrows();
        if (p instanceof Mage)   return "  |  Mana: "   + ((Mage)   p).getMana();
        return "";
    }

    static void printStats(Character p) {
        System.out.println("  HP: " + p.getHealth() +
                "  |  Damage: " + p.getDamage() +
                "  |  Stamina: " + p.getStamina() +
                extraResource(p));
    }

    static boolean askRestart() {
        System.out.println("\nWould you like to continue your legend?");
        System.out.println("  1. Restart the game");
        System.out.println("  2. Exit");
        int choice = -1;
        while (choice < 1 || choice > 2) {
            System.out.print("Choose (1-2): ");
            try {
                String restartRaw = reader.readLine();
                if (restartRaw == null) restartRaw = "2";
                choice = Integer.parseInt(restartRaw.trim());
            } catch (NumberFormatException | IOException ex) {
                System.out.println("  [Please enter 1 or 2.]");
            }
        }
        return choice == 1;
    }
}