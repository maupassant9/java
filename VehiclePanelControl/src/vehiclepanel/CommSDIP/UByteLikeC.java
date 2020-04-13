package vehiclepanel.CommSDIP;
//Internal Class for int
//Add function like in C language
//a simulation of addition for an integer of 16 bits variable
public class UByteLikeC {

    public int value;
    static final int MAX_UINT_VAL = 0xff;
    static final int MIN_UINT_VAL = 0;

    public UByteLikeC(int n){
        value = wrapVal(n);
    }

    //wrap the int value, simulate an overflow
    static public int wrapVal(int n){
        if(n > MAX_UINT_VAL) return wrapVal(n - MAX_UINT_VAL-1);
        if(n < MIN_UINT_VAL) return wrapVal(MAX_UINT_VAL+n+1);
        return n;
    }

    //addition
    public void add(int n){
        value = wrapVal(wrapVal(n)+value);
    }

    //delete: val - n 
    public void del(int n){
        n = wrapVal(n);
        value = wrapVal(value -n);
    }

    public void set(int n){
        value = wrapVal(n);
    }


}