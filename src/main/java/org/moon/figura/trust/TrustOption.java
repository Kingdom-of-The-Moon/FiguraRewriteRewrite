package org.moon.figura.trust;

public abstract class TrustOption <T> {

    public T value;
    private final String _name;

    protected TrustOption(String name) {
        _name = name;
    }

    public abstract T defaultValue();

    public String getValueString() {
        return value.toString();
    }
    public String getName() {return _name;}


    public abstract TrustOption getNewInstance(T val);

    public static class Range extends TrustOption<Integer> {
        public final int MinValue;
        public final int MaxValue;
        public final boolean ShowSteps;

        private final int _defaultValue;

        public Range(String name,int min, int max, int def, boolean showSteps) {
            super(name);
            MinValue = min;
            MaxValue = max;
            ShowSteps = showSteps;
            _defaultValue = def;
        }

        public Range(String name,int min, int max, int def) {
            this(name,min,max,def,false);
        }

        @Override
        public Integer defaultValue() {
            return _defaultValue;
        }

        @Override
        public Range getNewInstance(Integer val) {
            Range rng = new Range(getName(),MinValue, MaxValue, _defaultValue);
            rng.value = val;
            return rng;
        }
    }
    public static class Toggle extends TrustOption<Boolean> {
        private final boolean _defaultValue;

        public Toggle(String name, boolean def) {
            super(name);
            _defaultValue = def;
        }

        @Override
        public Boolean defaultValue() {
            return _defaultValue;
        }

        @Override
        public Toggle getNewInstance(Boolean val) {
            Toggle tgl = new Toggle(getName(),_defaultValue);
            tgl.value = val;
            return tgl;
        }
    }
}
