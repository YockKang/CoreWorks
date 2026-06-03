package com.main.CoreWorks.Recipe;

import com.badlogic.gdx.utils.*;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.*;

import java.util.Arrays;

public class Recipe {
    protected final Array<Resource> input;
    protected final Array<Integer> inputMult;
    protected final Array<Resource> output;
    protected final Array<Integer> outputMult;
    protected final Array<String> validGroups;
    protected final int duration;
    protected final String name;
    protected final String id;

    public Recipe(Array<Resource> inputs,
                  Array<Resource> outputs,
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

    public Recipe(Resource[] inputs,
                  Resource[] outputs,
                  Integer[] inputMultiple,
                  Integer[] outputMultiple,
                  String[] groups,
                  int dur,
                  String name,
                  String id) {
        this.input = new Array<Resource>(inputs);
        this.output = new Array<Resource>(outputs);
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
        this.input = new Array<Resource>(
            Arrays.stream(data.get("InputId").asStringArray())
                .map(ResourceDatabase::get)
                .toList()
                .toArray(r));
        this.output = new Array<Resource>(
            Arrays.stream(data.get("OutputId").asStringArray())
                .map(ResourceDatabase::get)
                .toList()
                .toArray(r));
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
            inputStr.append(input.get(i).toString()).append(" x");
            try {
                inputStr.append(inputMult.get(i).toString());
            } catch (Exception e) {
                inputStr.append("0");
            }
            inputStr.append(" ");
        }
        StringBuilder outputStr = new StringBuilder(" ");
        for (int i = 0; i < this.output.size; i++) {
            outputStr.append(output.get(i).toString()).append(" x");
            try {
                outputStr.append(outputMult.get(i).toString());
            } catch (Exception e) {
                outputStr.append("0");
            }
            outputStr.append(" ");
        }
        return this.name + ":\n" + "["  + inputStr + "]\n" + duration + " ticks\n["  + outputStr + "]";
    }

    public Array<Resource> getInputs() {
        return input;
    }

    public Array<Integer> getInputMultipliers() {
        return inputMult;
    }

    public Array<Resource> getOutputs() {
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
}
