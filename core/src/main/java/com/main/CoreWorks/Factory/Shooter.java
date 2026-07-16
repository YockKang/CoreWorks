package com.main.CoreWorks.Factory;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Factory.ResourceRequest.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.RunPersistence.CombatNode;
import com.main.CoreWorks.RunPersistence.RunState;
import com.main.CoreWorks.entities.Character;
import com.main.CoreWorks.entities.Enemy;
import com.main.CoreWorks.moveset.*;
import com.main.CoreWorks.util.Pair;

import java.util.Objects;

public class Shooter extends Building {

    protected Queue<Resource> magazine;
    protected int magSize;
    protected float baseDmg = 1f;
    protected int flatDmg = 0;
    protected int attackCount = 1;
    protected int attackRepeat = 1;
    protected String forceDmgType = null;
    protected Array<Pair<Integer, Float>> aoe = new Array<>();
    protected boolean randomTarget = false;

    public Shooter(int coolDown, int magSize, boolean[][] shape) {
        super(coolDown,
            new Array<ResourceBuffer>(0),
            new Array<ResourceBuffer>(0),
            shape,
            "shooter");
        this.magazine = new Queue<>(magSize);
        this.magSize = magSize;
    }

    public Shooter(JsonValue data) {
        super(data);
        System.out.println("generating " + this.name);
        this.magSize = data.getInt("MagSize");
        this.magazine = new Queue<>(magSize);
        this.baseDmg = data.getFloat("BaseDmg");
        if (data.get("AttackCount") != null) {
            attackCount = data.getInt("AttackCount");
        }
        if (data.get("Repeat") != null) {
            attackRepeat = data.getInt("Repeat");
        }
        if (data.get("Damage Type") != null) {
            forceDmgType = data.getString("Damage Type");
        }
        if (data.get("AoE") != null) {
            if (data.get("AoE").isString()) {
                if (Objects.equals(data.getString("AoE"), "Random")) {
                    aoe.add(new Pair<>(1, 1f));
                    randomTarget = true;
                } else if (Objects.equals(data.getString("AoE"), "All")) {
                    aoe.add(new Pair<>(0, 1f));
                } else {
                    aoe.add(new Pair<>(1, 1f));
                }
            } else {
                for (JsonValue val : data.get("AoE")) {
                    float[] vals = val.asFloatArray();
                    aoe.add(new Pair<>((int) vals[0], vals[1]));
                }

            }
        } else {
            aoe.add(new Pair<>(1, 1f));
        }
        aoe.sort((a, b) -> {
            int aSign = Integer.signum(a.first);
            int bSign = Integer.signum(b.first);
            if (aSign > bSign) {
                return 1;
            } else if (bSign > aSign) {
                return -1;
            } else {
                int comp = Integer.compare(a.first, b.first);
                if (comp != 0) {
                    return comp;
                } else {
                    return Float.compare(a.second, b.second);
                }
            }
        });
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(name).append(" #").append(idNum)
            .append("\nSpeed ")
            .append(getSpeed())
            .append("\nDamage:\n")
            .append(attackCount).append(" * (x * ").append(baseDmg).append(" + ").append(flatDmg).append(")")
            .append('\n')
            .append("Magazine\n")
            .append("<-First   Last->\n")
            .append(magazine)
            .toString();
    }

    @Override
    public Array<Move> updateEnabled(RunState runState) {
        currCooldown += getSpeed();
        Array<Move> moves = new Array<>();
        while (currCooldown >= cooldownTimer) {
            if (magazine.notEmpty()) {
                currCooldown -= cooldownTimer;
                moves.addAll(shoot(runState));
            } else {
                currCooldown = cooldownTimer - getSpeed();
                break;
            }
        }
        return moves;
    }

    public void addToAnythingQueue(Resource x) {
        addToMag(x);
    }

    public void addToMag(Resource x) {
        magazine.addLast(x);
    }

    private int calculateDmg(Resource r, float aoeMod, String dmgType, RunState runState) {
        int modExtraDmg = 0;
        if (r.getModifiers().containsKey("ExtraDmg")) {
            modExtraDmg = (int) r.getModifiers().get("ExtraDmg").getValue();
        }
        int untyped = (int) (baseDmg * r.getDmgMult() + flatDmg + modExtraDmg);
        int typed = 0;
        switch (dmgType) {
            case "Normal" -> {
                typed = untyped + (int) runState.getBonuses("BonusDmg", (x, y) -> (float) (x.intValue() + y.intValue()), 0);
            }
            case "True" -> {
                typed = untyped + (int) runState.getBonuses("BonusTrueDmg", (x, y) -> (float) (x.intValue() + y.intValue()), 0);
            }
            case "Poison" -> {
                typed = untyped + (int) runState.getBonuses("BonusPoisonDmg", (x, y) -> (float) (x.intValue() + y.intValue()), 0);
            }
        }
        typed = (int) ((float) typed * (1 + runState.getBonuses("GlobalMultiplier", Float::sum, 0)) * aoeMod);
        return typed;
    }

    private Move generateAtk(Resource ammo, Pair<Integer, Float> atk, RunState runState) {
        String dmgType;
        if (forceDmgType != null) {
            dmgType = forceDmgType;
        } else if (ammo.getModifiers().containsKey("DamageType")) {
            dmgType = ammo.getModifiers().get("DamageType").getStrValue();
        } else {
            dmgType = "Normal";
        }
        Move dmg = null;
        assert dmgType != null;
        CombatNode node = (CombatNode) runState.getCurrNode();
        boolean atkAll = false;
        Character target = null;
        if (Integer.signum(atk.first) > 0) {
            if (atk.first <= node.getEnemies().size) {
                target = node.getEnemies().get(atk.first - 1);
            } else {
                return null;
            }
        } else if (Integer.signum(atk.first) < 0) {
            if (atk.first >= -node.getEnemies().size) {
                target = node.getEnemies().get(node.getEnemies().size + atk.first);
            } else {
                return null;
            }
        } else if (Integer.signum(atk.first) == 0) {
            atkAll = true;
        }
        int damage = calculateDmg(ammo, atk.second, dmgType, runState);
        switch (dmgType) {
            case "Normal" -> {
                dmg = new DamageMove(damage, 0);
            }
            case "True" -> {
                dmg = new TrueDamageMove(damage, 0);
            }
            case "Poison" -> {
                dmg = new StatusEffectMove("Poison", damage, 4, 0.5f, true, false, 0);
            }
        }
        assert dmg != null;
        if (randomTarget) {
            dmg.setTarget(node.getEnemies().get(runState.getRandom().nextInt(node.getEnemies().size)));
        } else {
            dmg.setTarget(target);
            if (atkAll) {
                dmg.setHitAll(true);
            }
        }
        return dmg;
    }

    public Array<Move> shoot(RunState runState) {
        boolean willHitSomething = false;
        if (randomTarget) {
            willHitSomething = true;
        } else {
            for (Pair<Integer, Float> atk : aoe) {
                CombatNode node = (CombatNode) runState.getCurrNode();
                if (Integer.signum(atk.first) > 0) {
                    if (atk.first <= node.getEnemies().size) {
                        willHitSomething = true;
                        break;
                    }
                } else if (Integer.signum(atk.first) < 0) {
                    if (atk.first >= -node.getEnemies().size) {
                        willHitSomething = true;
                        break;
                    }
                } else if (Integer.signum(atk.first) == 0) {
                    willHitSomething = true;
                    break;
                }
            }
        }
        if (!willHitSomething) {
            return new Array<>();
        } else {
            Array<Move> result = new Array<>();
            Resource ammo = magazine.removeFirst();
            for (int i = 0; i < attackCount; i++) {
                for (Pair<Integer, Float> atk : aoe) {
                    Move move = generateAtk(ammo, atk, runState);
                    if (move != null) {
                        Character moveTarget = move.getTarget();
                        boolean hitAll = move.getHitAll();
                        for (int j = 0; j < attackRepeat; j++) {
                            result.add(move);
                            if (runState.hasBonus("ExtraAtkNormal")) {
                                Move extraMove = new DamageMove((int) (
                                    runState.getBonuses("ExtraAtkNormal", Float::sum) *
                                        (1 + runState.getBonuses("GlobalMultiplier", Float::sum))), 0);
                                extraMove.setTarget(moveTarget);
                                extraMove.setHitAll(hitAll);
                                result.add(extraMove);
                            }
                            if (runState.hasBonus("ExtraAtkTrue")) {
                                Move extraMove = new TrueDamageMove((int) (
                                    runState.getBonuses("ExtraAtkTrue", Float::sum) *
                                        (1 + runState.getBonuses("GlobalMultiplier", Float::sum))), 0);
                                extraMove.setTarget(moveTarget);
                                extraMove.setHitAll(hitAll);
                                result.add(extraMove);
                            }
                            if (runState.hasBonus("ExtraAtkPoison")) {
                                Move extraMove = new StatusEffectMove("Poison", (int) (
                                    runState.getBonuses("ExtraAtkPoison", Float::sum) *
                                        (1 + runState.getBonuses("GlobalMultiplier", Float::sum))),
                                    4, 0.5f, true, false, 0);
                                extraMove.setTarget(moveTarget);
                                extraMove.setHitAll(hitAll);
                                result.add(extraMove);
                            }
                        }
                    }
                }
            }
            return result;
        }
    }

    @Override
    public Array<ResourceRequest> generateDemandRequests(RunState runState) {
        Array<ResourceRequest> requests = new Array<>();
        int magMissing = magSize - magazine.size;
        if (magMissing > 0) {
            if (whitelist != null) {
                requests.add(new WhitelistRequest(this, magMissing, whitelist));
            } else {
                requests.add(new AnythingRequest(this, magMissing));
            }
        }
        return requests;
    }

    @Override
    public void clear() {
        super.clear();
        this.magazine.clear();
    }

    @Override
    public int getCapacityMult() {
        return magSize;
    }

    @Override
    public void setCapacityMult(int newCap) {
        magSize = newCap;
    }

    @Override
    public void changeCapacityMult(int delta) {
        magSize += delta;
    }

    public void changeBaseDamage(float delta) {
        baseDmg += delta;
    }

    public void changeFlatDamage(float delta) {
        flatDmg += (int) delta;
    }

    public float getBaseDmg() {
        return baseDmg;
    }

    public int getFlatDmg() {
        return flatDmg;
    }
}
