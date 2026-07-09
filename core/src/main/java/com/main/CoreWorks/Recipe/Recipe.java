package com.main.CoreWorks.Recipe;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.*;

import java.util.Arrays;

public class Recipe {
    protected final Array<String> input;
    protected final Array<Integer> inputMult;
    protected final Array<String> output;
    protected final Array<Integer> outputMult;
    protected final Array<String> validGroups;
    protected final int duration;
    protected final String name;
    protected final String id;

    public Recipe(Array<String> inputs,
                  Array<String> outputs,
                  Array<Integer> inputMultiple,
                  Array<Integer> outputMultiple,
                  Array<String> groups,
                  int dur,
                  String name,
                  String id) {
        this.input = inputs;
        this.inputMult = inputMultiple;
        this.output = outputs;
        this.outputMult = outputMultiple;
        this.validGroups = groups;
        this.duration = dur;
        this.name = name;
        this.id = id;
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    public Recipe(String[] inputs,
                  String[] outputs,
                  Integer[] inputMultiple,
                  Integer[] outputMultiple,
                  String[] groups,
                  int dur,
                  String name,
                  String id) {
        this.input = new Array<String>(inputs);
        this.output = new Array<String>(outputs);
        this.inputMult = new Array<Integer>(inputMultiple);
        this.outputMult = new Array<Integer>(outputMultiple);
        this.validGroups = new Array<String>(groups);
        this.duration = dur;
        this.name = name;
        this.id = id;
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    public Recipe(JsonValue data) {
        Resource[] r = new Resource[0];
        this.input = new Array<>(data.get("InputId").asStringArray());
        this.output = new Array<>(data.get("OutputId").asStringArray());
        this.inputMult = new Array<Integer>(
            Arrays.stream(
                data.get("InputMult")
                    .asIntArray())
                .boxed()
                .toArray(Integer[]::new));
        this.outputMult = new Array<Integer>(
            Arrays.stream(
                data.get("OutputMult")
                    .asIntArray())
                .boxed()
                .toArray(Integer[]::new));
        this.validGroups = new Array<String>(
            Arrays.stream(
                data.get("Groups")
                    .asStringArray())
                .toArray(String[]::new));
        this.duration = data.getInt("duration");
        this.name = data.getString("Name");
        this.id = data.getString("id");
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder inputStr = new StringBuilder(" ");
        for (int i = 0; i < this.input.size; i++) {
            inputStr.append(ResourceDatabase.getName(input.get(i)) ).append(" x");
            try {
                inputStr.append(inputMult.get(i).toString());
            } catch (Exception e) {
                inputStr.append("0");
            }
            inputStr.append(" ");
        }
        StringBuilder outputStr = new StringBuilder(" ");
        for (int i = 0; i < this.output.size; i++) {
            outputStr.append(ResourceDatabase.getName(output.get(i))).append(" x");
            try {
                outputStr.append(outputMult.get(i).toString());
            } catch (Exception e) {
                outputStr.append("0");
            }
            outputStr.append(" ");
        }
        return this.name + ":\n" + "["  + inputStr + "]\n" + duration + " ticks\n["  + outputStr + "]";
    }

    public Array<String> getInputs() {
        return input;
    }

    public Array<Integer> getInputMultipliers() {
        return inputMult;
    }

    public Array<String> getOutputs() {
        return output;
    }

    public Array<Integer> getOutputMultipliers() {
        return outputMult;
    }

    public int getDuration() {
        return duration;
    }

    public Array<String> getGroups() {
        return validGroups;
    }

    public String getName() {
        return name;
    }

    public Table displayStats(Skin skin) {
        Table mainTable = new Table(skin);
        mainTable.add(new Label(this.name, skin)).pad(2).row();


        if (input.size > 0) {
            Table inputTable = new Table(skin);
            for (int i = 0; i < this.input.size; i++) {
                StringBuilder inputStr = new StringBuilder(" ");
                inputStr.append(ResourceDatabase.getName(input.get(i)) ).append(" x");
                try {
                    inputStr.append(inputMult.get(i).toString());
                } catch (Exception e) {
                    inputStr.append("0");
                }
                Label label = new Label(inputStr, skin);
                Table labelBox = new Table(skin);
                labelBox.setBackground("default-round");
                labelBox.add(label);
                inputTable.add(labelBox).pad(2);
            }
            mainTable.add(inputTable).pad(2).row();
        }

        mainTable.add(new Label(duration + " ticks", skin)).pad(2).row();

        if (output.size > 0) {
            Table outputTable = new Table(skin);
            for (int i = 0; i < this.output.size; i++) {
                StringBuilder outputStr = new StringBuilder(" ");
                outputStr.append(ResourceDatabase.getName(output.get(i)) ).append(" x");
                try {
                    outputStr.append(outputMult.get(i).toString());
                } catch (Exception e) {
                    outputStr.append("0");
                }
                Label label = new Label(outputStr, skin);
                Table labelBox = new Table(skin);
                labelBox.setBackground("default-round");
                labelBox.add(label);
                outputTable.add(labelBox).pad(2);
            }
            mainTable.add(outputTable).pad(2).row();
        }

        return mainTable;
    }
}
