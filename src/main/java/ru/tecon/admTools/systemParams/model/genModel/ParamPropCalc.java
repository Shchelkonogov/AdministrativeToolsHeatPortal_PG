package ru.tecon.admTools.systemParams.model.genModel;

import ru.tecon.admTools.systemParams.model.statAggr.StatAggrTable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author Maksim Shchelkonogov
 * 03.07.2023
 */
public class ParamPropCalc {

    private String formula;
    private List<PropRow> props = new ArrayList<>();

    private List<ObjectParam> paramListForChoice = new ArrayList<>();

    public ParamPropCalc() {
    }

    public ParamPropCalc(String formula) {
        this();
        this.formula = formula;
    }

    public void addParamProp(String variable) {
        props.add(new PropRow(variable));
    }

    public void addParamProp(String variable, ObjectParam param, StatAggrTable statAggr) {
        props.add(new PropRow(variable, param, statAggr));
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }

    public void setProps(List<PropRow> props) {
        this.props = props;
    }

    public List<PropRow> getProps() {
        return props;
    }

    public List<ObjectParam> getParamListForChoice() {
        return paramListForChoice;
    }

    public void setParamListForChoice(List<ObjectParam> paramListForChoice) {
        this.paramListForChoice = paramListForChoice;
    }

    public static class PropRow {

        private final String variable;
        private ObjectParam param;
        private StatAggrTable statAggr;

        private List<StatAggrTable> statAggrListForChoice = new ArrayList<>();

        public PropRow(String variable) {
            this.variable = variable;
        }

        private PropRow(String variable, ObjectParam param, StatAggrTable statAggr) {
            this(variable);
            this.param = param;
            this.statAggr = statAggr;
        }

        public String getVariable() {
            return variable;
        }

        public ObjectParam getParam() {
            return param;
        }

        public void setParam(ObjectParam param) {
            this.param = param;
        }

        public StatAggrTable getStatAggr() {
            return statAggr;
        }

        public void setStatAggr(StatAggrTable statAggr) {
            this.statAggr = statAggr;
        }

        public List<StatAggrTable> getStatAggrListForChoice() {
            return statAggrListForChoice;
        }

        public void setStatAggrListForChoice(List<StatAggrTable> statAggrListForChoice) {
            this.statAggrListForChoice = statAggrListForChoice;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", PropRow.class.getSimpleName() + "[", "]")
                    .add("variable='" + variable + "'")
                    .add("param=" + param)
                    .add("statAggr=" + statAggr)
                    .toString();
        }
    }
}
