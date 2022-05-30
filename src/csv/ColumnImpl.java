package csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ColumnImpl implements Column {
    private ArrayList<Object> contents=new ArrayList<>();

    //생성자
    ColumnImpl(String header){ contents.add(header); }

    public String getHeader(){return (String) contents.get(0);}

    /* cell 값을 String으로 반환 */
    public String getValue(int index){
        if (contents.get(index).equals(" ")){
            return "null";
        }
        return (String)contents.get(index);
    }

    /**
     * @param index
     * @param t 가능한 값으로 Double.class, Integer.class
     * @return Double 혹은 Integer로 반환 불가능할 시, 예외 발생
     */
    public <T extends Number> T getValue(int index, Class<T> t){
        try{
            if(t.getTypeName().equals("java.lang.Integer"))
                return t.cast(Integer.valueOf(getValue(index)));
            else
                return t.cast(Double.valueOf(getValue(index)));
        }
        catch (NumberFormatException e){
            return t.cast(null);
        }
    }

    public void setValue(int index, String value){
        try{
            contents.set(index, value);
        }
        catch (IndexOutOfBoundsException e){
            contents.add(index, value);
        }
    }

    /**
     * @param value double, int 리터럴을 index의 cell로 건네고 싶을 때 사용
     */
    public <T extends Number> void setValue(int index, T value){
        try{
            String input=value.toString();
            contents.set(index, input);
        }
        catch (IndexOutOfBoundsException e){
            String input=value.toString();
            contents.add(index, input);
        }
    }

    public long getNullCount(){
        long count=0;
        for(int i=1;i< contents.size();i++){
            if(contents.get(i)==" ")
                count++;
        }
        return count;
    }

    /**
     * @return null 포함 모든 cell 개수 반환
     */
    public int count(){ return contents.size()-1;}

    // 아래 7개 메소드는 String 타입 컬럼에 대해서 수행 시, 예외 발생 시켜라.
    public double getNumericMin(){
        try {
            Double min=getValue(1,Double.class);

            for (int i = 2; i < contents.size(); i++) {
                if (min > getValue(i, Double.class)) {
                    min = getValue(i, Double.class);
                }
            }

            return  min;
        }
        catch (NullPointerException e){
            Double min=0.0;
            for(int i=1;i<contents.size();i++){
                if(getValue(i,Double.class)!=null) {
                    min=getValue(i,Double.class);
                    break;
                }
            }


            for (int i = 2; i < contents.size(); i++) {
                if (getValue(i,Double.class)!=null) {
                    if (min > getValue(i, Double.class)) {
                        min = getValue(i, Double.class);
                    }
                }
            }
            return  min;

        }
    }

    public double getNumericMax(){
        try {
            Double max=getValue(1,Double.class);

            for (int i = 2; i < contents.size(); i++) {
                if (max < getValue(i, Double.class)) {
                    max = getValue(i, Double.class);
                }
            }

            return  max;
        }
        catch (NullPointerException e){
            Double max=0.0;
            for(int i=1;i<contents.size();i++){
                if(getValue(i,Double.class)!=null) {
                    max=getValue(i,Double.class);
                    break;
                }
            }


            for (int i = 2; i < contents.size(); i++) {
                if (getValue(i,Double.class)!=null) {
                    if (max < getValue(i, Double.class)) {
                        max = getValue(i, Double.class);
                    }
                }
            }
            return  max;

        }
    }

    public double getMean(){
        try {
            Double sum=0.0;
            for(int i=1;i<contents.size();i++){
                sum+=getValue(i,Double.class);
            }
            return  Math.round(sum/getNumericCount()*1000000)/1000000.0;
        }
        catch (NullPointerException e){
            Double sum=0.0;
            for(int i=1;i<contents.size();i++){
                if(getValue(i,Double.class)==null)
                    continue;
                sum+=getValue(i,Double.class);
            }
            return  Math.round(sum/getNumericCount()*1000000)/1000000.0;
        }
    }

    public double getStd(){

        double sum=0;
        double mean=getMean();
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null)
                continue;
            sum+=Math.pow((getValue(i,Double.class)-mean),2);
        }
        return Math.round(Math.sqrt(sum/(getNumericCount()-1))*1000000)/1000000.0;
    }

    public double getQ1(){
        Vector<Double> temp=new Vector<>();
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null)
                continue;
            temp.add(getValue(i,Double.class));
        }
        for(int i=0;i<temp.size()-1;i++){
            for(int j=i+1;j<temp.size();j++){
                if(temp.get(i)>temp.get(j)) {
                    double k =temp.get(i);
                    temp.set(i,temp.get(j));
                    temp.set(j,k);
                }
            }
        }
        return temp.get((int)getNumericCount()/4);
    }

    public double getMedian(){

        Vector<Double> temp=new Vector<>();
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null)
                continue;
            temp.add(getValue(i,Double.class));
        }
        for(int i=0;i<temp.size()-1;i++){
            for(int j=i+1;j<temp.size();j++){
                if(temp.get(i)>temp.get(j)) {
                    double k =temp.get(i);
                    temp.set(i,temp.get(j));
                    temp.set(j,k);
                }
            }
        }
        return temp.get((int)getNumericCount()/2);
    }

    public double getQ3(){

        Vector<Double> temp=new Vector<>();
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null)
                continue;
            temp.add(getValue(i,Double.class));
        }
        for(int i=0;i<temp.size()-1;i++){
            for(int j=i+1;j<temp.size();j++){
                if(temp.get(i)>temp.get(j)) {
                    double k =temp.get(i);
                    temp.set(i,temp.get(j));
                    temp.set(j,k);
                }
            }
        }
        return temp.get((3*(int)getNumericCount()/4));
    }

    /**
     * @return int 혹은 double로 평가될 수 있는 cell의 개수
     */
    public long getNumericCount(){
        long count=contents.size()-1;
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null) count--;
        }
        return count;
    }

    // 아래 2개 메소드는 1개 cell이라도 치환했으면, true 반환.
    public boolean fillNullWithMean(){
        double mean=getMean();
        boolean check=false;

        //String 컬럼일 경우 false 반환
        for(int i=1;i<contents.size();i++) {
            if (getValue(i, Double.class) == null) {
                if (!getValue(i).equals("null")) return check;
            }
        }

        for(int i=1; i<contents.size();i++){
            if(getValue(i).equals("null")){
                setValue(i,mean);
                check=true;
            }
        }
        return check;
    }

    public boolean fillNullWithZero(){
        boolean check=false;

        //String 컬럼일 경우 false 반환
        for(int i=1;i<contents.size();i++) {
            if (getValue(i, Double.class) == null) {
                if (!getValue(i).equals("null")) return check;
            }
        }

        for(int i=1; i<contents.size();i++){
            if(getValue(i).equals("null")){
                setValue(i,"0");
                check=true;
            }
        }
        return check;
    }

    // 아래 3개 메소드는 null 값은 메소드 호출 후에도 여전히 null.
    // standardize()와 normalize()는 String 타입 컬럼에 대해서는 false 반환
    // factorize()는 컬럼 타입과 무관하게 null 제외하고 2가지 값만으로 구성되었다면 수행된다. 조건에 부합하여 수행되었다면 true 반환
    public boolean standardize(){
        boolean check=false;

        //String 컬럼일 경우 false 반환
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null&&!getValue(i).equals("null"))
                return check;
        }

        double mean=getMean();
        double std=getStd();
        for(int i=1; i<contents.size();i++){
            if(getValue(i,Double.class)!=null){
                contents.set(i,Double.toString(Math.round((getValue(i,Double.class)-mean)/std*1000000)/1000000.0));
                check=true;
            }
        }
        return check;
    }

    public boolean normalize(){
        boolean check=false;

        //String 컬럼일 경우 false 반환
        for(int i=1;i<contents.size();i++){
            if(getValue(i,Double.class)==null&&!getValue(i).equals("null"))
                return check;
        }

        double max=getNumericMax();
        double min=getNumericMin();
        for(int i=1; i<contents.size();i++){
            if(getValue(i,Double.class)!=null){
                contents.set(i,Double.toString(Math.round((getValue(i,Double.class)-min)/(max-min)*1000000)/1000000.0));
                check=true;
            }
        }
        return check;
    }

    public boolean factorize(){
        boolean check=false;

        String comp1=getValue(1);
        String comp2=getValue(1);

        for(int i=2; i<contents.size();i++){
            if(!comp1.equals(getValue(i))){
                comp2=getValue(i);
                break;
            }
        }
        for(int i=1; i<contents.size();i++){
            if(comp1.equals(getValue(i))^comp2.equals(getValue(i))){ continue; }
            else return check;
        }
        for(int i=1; i<contents.size();i++){
            if(comp1.equals(getValue(i))){ setValue(i,0); }
            else setValue(i,1);
        }

        return true;
    }

}
