package csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CSVs {
    /**
     * @param isFirstLineHeader csv 파일의 첫 라인을 헤더(타이틀)로 처리할까요?
     */
    public static Table createTable(File csv, boolean isFirstLineHeader) throws FileNotFoundException {

        Scanner scanner=new Scanner(csv);
        List<String>contents=new ArrayList<>();
        String headers=null;
        String temp;


        if(isFirstLineHeader){
            headers=scanner.nextLine();
        }
        while(scanner.hasNext()){
            temp=scanner.nextLine();
            contents.add(temp);
        }

        return new TableImpl(headers,contents);


    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table sort(Table table, int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {

        String header="";
        for(int i=0; i< table.getColumnCount();i++){
            header+=table.getColumn(i).getHeader();
            if(i!=table.getColumnCount()-1)
                header+=",";
        }
        ArrayList<String> contents=new ArrayList<String>();

        for(int i=1;i<=table.getRowCount();i++){
            String temp="";
            for(int j=0;j<  table.getColumnCount();j++){
                temp+= table.getColumn(j).getValue(i);
                if(j!=table.getColumnCount()-1)
                    temp+=",";
            }
            contents.add(temp);
        }

        Table temp=new TableImpl(header,contents);
        temp.sort(byIndexOfColumn, isAscending, isNullFirst);

        return temp;
    }

    /**
     * @return 새로운 Table 객체를 반환한다. 즉, 첫 번째 매개변수 Table은 변경되지 않는다.
     */
    public static Table shuffle(Table table) {
        String header="";
        for(int i=0; i< table.getColumnCount();i++){
            header+=table.getColumn(i).getHeader();
            if(i!=table.getColumnCount()-1)
                header+=",";
        }
        ArrayList<String> contents=new ArrayList<String>();

        for(int i=1;i<=table.getRowCount();i++){
            String temp="";
            for(int j=0;j<  table.getColumnCount();j++){
                temp+= table.getColumn(j).getValue(i);
                if(j!=table.getColumnCount()-1)
                    temp+=",";
            }
            contents.add(temp);
        }

        Table temp=new TableImpl(header,contents);
        temp.shuffle();

        return temp;
    }

}
