package datatypes;

import common.Constants;
import datatypes.base.DT_Numeric;

/**
 * Created by dakle on 10/4/17.
 */
public class DT_BigInt extends DT_Numeric<Long> {

    public DT_BigInt() {
        this(0, true);
    }

    public DT_BigInt(Long value) {
        this(value == null ? 0 : value, value == null);
    }

    public DT_BigInt(long value, boolean isNull) {
        super(Constants.BIG_INT_SERIAL_TYPE_CODE, Constants.EIGHT_BYTE_NULL_SERIAL_TYPE_CODE, Long.BYTES);
        this.value = value;
        this.isNull = isNull;
    }

    @Override
    public void increment(Long value) {
        this.value += value;
    }

    @Override
    public boolean compare(DT_Numeric<Long> object2, short condition) {
        if(value == null) return false;
        switch (condition) {
            case DT_Numeric.EQUALS:
                return value == object2.getValue();

            case DT_Numeric.GREATER_THAN:
                return value > object2.getValue();

            case DT_Numeric.LESS_THAN:
                return value < object2.getValue();

            case DT_Numeric.GREATER_THAN_EQUALS:
                return value >= object2.getValue();

            case DT_Numeric.LESS_THAN_EQUALS:
                return value <= object2.getValue();

            default:
                return false;
        }
    }

    public boolean compare(DT_TinyInt object2, short condition) {
        DT_BigInt object = new DT_BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(DT_SmallInt object2, short condition) {
        DT_BigInt object = new DT_BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }

    public boolean compare(DT_Int object2, short condition) {
        DT_BigInt object = new DT_BigInt(object2.getValue(), false);
        return this.compare(object, condition);
    }
}
