package dc.gtest.vortex.unused;
import android.text.InputFilter ;
import android.text.Spanned ;
import android.text.TextUtils;

public class MinMaxFilter implements InputFilter {
    private final double mMinValue;
    private final double mMaxValue;

    private static final double MIN_VALUE_DEFAULT = Double.MIN_VALUE;
    private static final double MAX_VALUE_DEFAULT = Double.MAX_VALUE;

    public MinMaxFilter(Double min, Double max) {
        this.mMinValue = (min != null ? min : MIN_VALUE_DEFAULT);
        this.mMaxValue = (max != null ? max : MAX_VALUE_DEFAULT);
    }

    public MinMaxFilter(Integer min, Integer max) {
        this.mMinValue = (min != null ? min : MIN_VALUE_DEFAULT);
        this.mMaxValue = (max != null ? max : MAX_VALUE_DEFAULT);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            String replacement = source.subSequence(start, end).toString();
            String newVal = dest.subSequence(0, dstart).toString() + replacement
                    + dest.subSequence(dend, dest.length()).toString();

            // check if there are leading zeros
            if (newVal.matches("0\\d+.*"))
                if (TextUtils.isEmpty(source))
                    return dest.subSequence(dstart, dend);
                else
                    return "";

            // check range
            double input = Double.parseDouble(newVal);
            if (!isInRange(mMinValue, mMaxValue, input))
                if (TextUtils.isEmpty(source))
                    return dest.subSequence(dstart, dend);
                else
                    return "";

            return null;
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace() ;
        }
        return "";
    }

    private boolean isInRange(double a, double b, double c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
//public class MinMaxFilter implements InputFilter {
//    private final int mIntMin;
//    private final int mIntMax ;
//    public MinMaxFilter ( int minValue , int maxValue) {
//        this . mIntMin = minValue ;
//        this . mIntMax = maxValue ;
//    }
//    public MinMaxFilter (String minValue , String maxValue) {
//        this . mIntMin = Integer. parseInt (minValue) ;
//        this . mIntMax = Integer. parseInt (maxValue) ;
//    }
//    @Override
//    public CharSequence filter (CharSequence source , int start , int end , Spanned dest ,int dstart , int dend) {
//        try {
//            int input = Integer. parseInt (dest.toString() + source.toString()) ;
//            if (isInRange( mIntMin , mIntMax , input))
//                return null;
//        } catch (NumberFormatException e) {
//            e.printStackTrace() ;
//        }
//        return "" ;
//    }
//    private boolean isInRange ( int a , int b , int c) {
//        return b > a ? c >= a && c <= b : c >= b && c <= a ;
//    }
//}