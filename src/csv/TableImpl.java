package csv;

import javax.print.attribute.standard.NumberOfInterveningJobs;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TableImpl  implements  Table {

        private ArrayList<Column> columns = new ArrayList<Column>();

        //생성자
        TableImpl(String headers, List<String> contents) {


            if (headers != null) {
                String[] tokens = headers.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    columns.add(new ColumnImpl(tokens[i]));
                }

                for (int i = 0; i < contents.size(); i++) {
                    String[] temp = contents.get(i).split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                    for (int j = 0; j < tokens.length; j++) {
                        if (temp[j].equals("")) columns.get(j).setValue(i + 1, " ");
                        else columns.get(j).setValue(i + 1, temp[j]);
                    }
                }

            } else {
                String[] temp = contents.get(0).split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for (int i = 0; i < temp.length; i++) {
                    columns.add(new ColumnImpl(null));
                }


                for (int i = 0; i < contents.size(); i++) {
                    temp = contents.get(i).split(",");
                    for (int j = 0; j < temp.length; j++) {
                        if (temp[j].equals("")) columns.get(j).setValue(i + 1, " ");
                        else columns.get(j).setValue(i + 1, temp[j]);
                    }
                }
            }


        }

        public void print() {
            int[] space = new int[columns.size()];

            //띄어쓰기 공간 계산하기
            for (int i = 0; i < space.length; i++) {
                space[i] = columns.get(i).getHeader().length();
            }
            for (int i = 0; i < space.length; i++) {
                for (int j = 1; j <= getRowCount(); j++) {
                    if (space[i] < columns.get(i).getValue(j).length())
                        space[i] = columns.get(i).getValue(j).length();
                }
            }

            //header 출력
            for (int i = 0; i < space.length; i++) {
                int j = space[i] - columns.get(i).getHeader().length();
                String sp = " ";
                System.out.print(sp.repeat(j) + columns.get(i).getHeader() + " | ");
            }
            System.out.println();

            //본문 출력
            for (int i = 1; i <= getRowCount(); i++) {
                for (int j = 0; j < space.length; j++) {
                    int k = space[j] - columns.get(j).getValue(i).length();
                    String sp = " ";
                    System.out.print(sp.repeat(k) + columns.get(j).getValue(i) + " | ");
                }
                System.out.println();
            }
        }

        /**
         * String 타입 컬럼이더라도,
         * 그 컬럼에 double로 처리할 수 있는 값이 있다면,
         * 그 값을 대상으로 해당 컬럼 통계량을 산출
         */
        public Table getStats() {
            List<Column> temp = new ArrayList<Column>();

            //double로 처리할 수 있는 칼럼들 고르기
            for (int i = 0; i < columns.size(); i++) {
                for (int j = 1; j < getRowCount() + 1; j++) {
                    if (columns.get(i).getValue(j, Double.class) != null) {
                        temp.add(columns.get(i));
                        break;
                    }

                }
            }


            //header 뽑기
            String headers = " ";
            for (int i = 0; i < temp.size(); i++) {
                headers += "," + temp.get(i).getHeader();
            }


            String[] rows = {"count", "mean", "std", "min", "25%", "50%", "75%", "max"};

            List<String> contents = new ArrayList<>();
            String sp = "";

            sp = rows[0];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + (temp.get(j).getNumericCount());
            }
            contents.add(sp);

            sp = rows[1];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getMean();
            }
            contents.add(sp);

            sp = rows[2];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getStd();
            }
            contents.add(sp);

            sp = rows[3];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getNumericMin();
            }
            contents.add(sp);

            sp = rows[4];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getQ1();
            }
            contents.add(sp);

            sp = rows[5];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getMedian();
            }
            contents.add(sp);

            sp = rows[6];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getQ3();
            }
            contents.add(sp);

            sp = rows[7];
            for (int j = 0; j < temp.size(); j++) {
                sp += "," + temp.get(j).getNumericMax();
            }
            contents.add(sp);

            return new TableImpl(headers, contents);

        }

        /**
         * @return 처음 (최대)5개 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table head() {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();

            for (int i = 1; i <= 5; i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @return 처음 (최대)lineCount개 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table head(int lineCount) {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();

            for (int i = 1; i <= lineCount; i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @return 마지막 (최대)5개 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table tail() {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();

            for (int i = getRowCount() - 4; i <= getRowCount(); i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @return 마지막 (최대)lineCount개 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table tail(int lineCount) {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();

            for (int i = getRowCount() - lineCount + 1; i <= getRowCount(); i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @param beginIndex 포함(이상)
         * @param endIndex   미포함(미만)
         * @return 검색 범위에 해당하는 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table selectRows(int beginIndex, int endIndex) {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();

            if (beginIndex == 0) {
                beginIndex++;
                endIndex++;
                for (int i = beginIndex; i < endIndex; i++) {
                    String temp = "";
                    for (int j = 0; j < columns.size(); j++) {
                        temp += columns.get(j).getValue(i);
                        if (j != columns.size() - 1)
                            temp += ",";
                    }
                    contents.add(temp);
                }
                return new TableImpl(header, contents);
            }

            for (int i = beginIndex; i < endIndex; i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @return 검색 인덱스에 해당하는 행으로 구성된 새로운 Table 생성 후 반환
         */
        public Table selectRowsAt(int... indices) {
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }
            ArrayList<String> contents = new ArrayList<String>();


            for (int i = 0; i < indices.length; i++) {
                String temp = "";
                for (int j = 0; j < columns.size(); j++) {
                    temp += columns.get(j).getValue(indices[i]);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }

            return new TableImpl(header, contents);
        }

        /**
         * @param beginIndex 포함(이상)
         * @param endIndex   미포함(미만)
         * @return 검색 범위에 해당하는 열로 구성된 새로운 Table 생성 후 반환
         */
        public Table selectColumns(int beginIndex, int endIndex) {
            String header = "";
            for (int i = beginIndex; i < endIndex; i++) {
                header += columns.get(i).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }

            ArrayList<String> contents = new ArrayList<String>();

            for (int i = 0; i < getRowCount(); i++) {
                String temp = "";
                for (int j = beginIndex; j < endIndex; j++) {
                    temp += columns.get(j).getValue(i);
                    if (j != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @return 검색 인덱스에 해당하는 열로 구성된 새로운 Table 생성 후 반환
         */
        public Table selectColumnsAt(int... indices) {
            String header = "";
            for (int i = 0; i < indices.length; i++) {
                header += columns.get(indices[i]).getHeader();
                if (i != columns.size() - 1)
                    header += ",";
            }

            ArrayList<String> contents = new ArrayList<String>();

            for (int i = 0; i < getRowCount(); i++) {
                String temp = "";
                for (int j = 0; j < indices.length; j++) {
                    temp += columns.get(indices[j]).getValue(i);
                    if (indices[j] != columns.size() - 1)
                        temp += ",";
                }
                contents.add(temp);
            }
            return new TableImpl(header, contents);
        }

        /**
         * @param
         * @return 검색 조건에 해당하는 행으로 구성된 새로운 Table 생성 후 반환, 제일 나중에 구현 시도하세요.
         */
        public <T> Table selectRowsBy(String columnName, Predicate<T> predicate) {


            int indexOfSearchHeader = 0;
            String header = "";
            for (int i = 0; i < columns.size(); i++) {
                header += columns.get(i).getHeader();
                if (columns.get(i).getHeader().equals(columnName)) indexOfSearchHeader = i;
                if (i != columns.size() - 1)
                    header += ",";
            }

            ArrayList<String> contents = new ArrayList<String>();


            try {
                for (int i = 1; i <= getRowCount(); i++) {
                    String temp = "";
                    if (predicate.test((T) columns.get(indexOfSearchHeader).getValue(i))) {
                        for (int j = 0; j < columns.size(); j++) {
                            temp += columns.get(j).getValue(i);
                            if (j != columns.size() - 1)
                                temp += ",";
                        }
                        contents.add(temp);
                    }

                }
            } catch (ClassCastException e) {
                try {
                    for (int i = 1; i <= getRowCount(); i++) {
                        String temp = "";
                        if (columns.get(indexOfSearchHeader).getValue(i, Integer.class) == null)
                            continue;
                        if (predicate.test((T) columns.get(indexOfSearchHeader).getValue(i, Integer.class))) {
                            for (int j = 0; j < columns.size(); j++) {
                                temp += columns.get(j).getValue(i);
                                if (j != columns.size() - 1)
                                    temp += ",";
                            }
                            contents.add(temp);
                        }

                    }
                } catch (ClassCastException ex) {
                    for (int i = 1; i <= getRowCount(); i++) {
                        String temp = "";
                        if (columns.get(indexOfSearchHeader).getValue(i, Double.class) == null)
                            continue;
                        if (predicate.test((T) columns.get(indexOfSearchHeader).getValue(i, Double.class))) {
                            for (int j = 0; j < columns.size(); j++) {
                                temp += columns.get(j).getValue(i);
                                if (j != columns.size() - 1)
                                    temp += ",";
                            }
                            contents.add(temp);
                        }
                    }
                }
            }


            return new TableImpl(header, contents);
        }

        /**
         * @return 원본 Table이 정렬되어 반환된다.
         */
        public Table sort(int byIndexOfColumn, boolean isAscending, boolean isNullFirst) {
            if (isNullFirst) {
                //null 앞으로
                int down = 1;
                int up = getRowCount();
                while (true) {
                    if (down == up)
                        break;
                    if (columns.get(byIndexOfColumn).getValue(down).equals("null")) {
                        down++;
                        continue;
                    }
                    if (!columns.get(byIndexOfColumn).getValue(up).equals("null")) {
                        up--;
                        continue;
                    }
                    for (int k = 0; k < columns.size(); k++) {
                        String tempdown = columns.get(k).getValue(down);
                        if (tempdown.equals("null"))
                            tempdown = " ";
                        String tempup = columns.get(k).getValue(up);
                        if (tempup.equals("null"))
                            tempup = " ";
                        columns.get(k).setValue(down, tempup);
                        columns.get(k).setValue(up, tempdown);
                    }
                }

                //문자열 정렬
                if (getColumnType(byIndexOfColumn) == "String") {
                    //오름차순
                    if (isAscending) {
                        int rowcount = getRowCount();
                        int startcount = (int) columns.get(byIndexOfColumn).getNullCount() + 1;
                        for (int i = startcount; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i).compareTo(columns.get(byIndexOfColumn).getValue(j)) > 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                    //내림차순
                    else {
                        int rowcount = getRowCount();
                        int startcount = (int) columns.get(byIndexOfColumn).getNullCount() + 1;
                        for (int i = startcount; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i).compareTo(columns.get(byIndexOfColumn).getValue(j)) < 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                }
                //숫자 정렬
                else {
                    //오름차순
                    if (isAscending) {
                        int rowcount = getRowCount();
                        int startcount = (int) columns.get(byIndexOfColumn).getNullCount() + 1;

                        for (int i = startcount; i < rowcount; i++) {
                            for (int j = i + 1; j <= getRowCount(); j++) {
                                if (columns.get(byIndexOfColumn).getValue(i, Double.class).compareTo(columns.get(byIndexOfColumn).getValue(j, Double.class)) > 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                    //내림차순
                    else {
                        int rowcount = getRowCount();
                        int startcount = (int) columns.get(byIndexOfColumn).getNullCount() + 1;
                        for (int i = startcount; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i, Double.class).compareTo(columns.get(byIndexOfColumn).getValue(j, Double.class)) < 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                }
            } else {
                //null을 뒤로
                int down = 1;
                int up = getRowCount();
                while (true) {
                    if (down == up)
                        break;
                    if (!columns.get(byIndexOfColumn).getValue(down).equals("null")) {
                        down++;
                        continue;
                    }
                    if (columns.get(byIndexOfColumn).getValue(up).equals("null")) {
                        up--;
                        continue;
                    }
                    for (int k = 0; k < columns.size(); k++) {
                        String tempdown = columns.get(k).getValue(down);
                        if (tempdown.equals("null"))
                            tempdown = " ";
                        String tempup = columns.get(k).getValue(up);
                        if (tempup.equals("null"))
                            tempup = " ";
                        columns.get(k).setValue(down, tempup);
                        columns.get(k).setValue(up, tempdown);
                    }

                }
                //문자열 정렬
                if (getColumnType(byIndexOfColumn) == "String") {
                    //오름차순
                    if (isAscending) {
                        int rowcount = (int) columns.get(byIndexOfColumn).getNumericCount();
                        for (int i = 1; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i).compareTo(columns.get(byIndexOfColumn).getValue(j)) > 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                    //내림차순
                    else {
                        int rowcount = (int) columns.get(byIndexOfColumn).getNumericCount();
                        for (int i = 1; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i).compareTo(columns.get(byIndexOfColumn).getValue(j)) < 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                }
                //숫자 정렬
                else {
                    //오름차순
                    if (isAscending) {
                        int rowcount = (int) columns.get(byIndexOfColumn).getNumericCount();
                        for (int i = 1; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i, Double.class).compareTo(columns.get(byIndexOfColumn).getValue(j, Double.class)) > 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                    //내림차순
                    else {
                        int rowcount = (int) columns.get(byIndexOfColumn).getNumericCount();
                        for (int i = 1; i < rowcount; i++) {
                            for (int j = i + 1; j <= rowcount; j++) {
                                if (columns.get(byIndexOfColumn).getValue(i, Double.class).compareTo(columns.get(byIndexOfColumn).getValue(j, Double.class)) < 0)
                                    for (int k = 0; k < columns.size(); k++) {
                                        String temp = columns.get(k).getValue(i);
                                        columns.get(k).setValue(i, columns.get(k).getValue(j));
                                        columns.get(k).setValue(j, temp);
                                    }
                            }
                        }
                    }
                }


            }

            return this;
        }

        /**
         * @return 원본 Table이 무작위로 뒤섞인 후 반환된다. 말 그대로 랜덤이어야 한다. 즉, 랜덤 로직이 존재해야 한다.
         */
        public Table shuffle() {
            for (int i = 1; i <= getRowCount(); i++) {
                int rand = (int) (Math.random() * getRowCount()) + 1;

                while (i > rand) {
                    rand = (int) (Math.random() * getRowCount()) + 1;
                }

                for (int j = 0; j < columns.size(); j++) {
                    String temp = columns.get(j).getValue(i);
                    columns.get(j).setValue(i, columns.get(j).getValue(rand));
                    columns.get(j).setValue(rand, temp);
                }
            }
            return this;
        }

        public int getRowCount() {
            return columns.get(0).count();
        }

        public int getColumnCount() {
            return columns.size();
        }

        /**
         * @return 원본 Column이 반환된다. 따라서, 반환된 Column에 대한 조작은 원본 Table에 영향을 끼친다.
         */
        public Column getColumn(int index) {
            return columns.get(index);
        }

        /**
         * @return 원본 Column이 반환된다. 따라서, 반환된 Column에 대한 조작은 원본 Table에 영향을 끼친다.
         */
        public Column getColumn(String name) {
            for (int i = 0; i < columns.size(); i++) {
                if (name.equals(columns.get(i).getHeader()))
                    return columns.get(i);
            }
            return null;
        }

        /**
         * String 타입 컬럼들에는 영향을 끼치지 않는다.
         * double 혹은 int 타입 컬럼들에 한해서 null 값을 mean 값으로 치환한다.
         * 이 연산 후, int 타입 컬럼에 mean으로 치환된 cell이 있을 경우, 이 컬럼은 double 타입 컬럼으로 바뀐다.
         * 왜냐하면, mean 값이 double이기 때문이다.
         *
         * @return 테이블에 mean으로 치환한 cell이 1개라도 발생했다면, true 반환
         */
        public boolean fillNullWithMean() {
            boolean check = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).fillNullWithMean()) check = true;
            }
            return check;
        }

        /**
         * String 타입 컬럼들에는 영향을 끼치지 않는다.
         * double 혹은 int 타입 컬럼들에 한해서 null 값을 0으로 치환한다.
         * 이 연산 후, int 타입 혹은 double 타입 컬럼 모두 그 타입이 유지된다.
         *
         * @return 테이블에 0으로 치환한 cell이 1개라도 발생했다면, true 반환
         */
        public boolean fillNullWithZero() {
            boolean check = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).fillNullWithZero()) check = true;
            }
            return check;
        }

        /**
         * 평균 0, 표준편자 1인 컬럼으로 바꾼다. (null은 연산 후에도 null로 유지된다. 즉, null은 연산 제외)
         * String 타입 컬럼들에는 영향을 끼치지 않는다.
         * double 혹은 int 타입 컬럼들에 한해서 수행된다.
         * 이 연산 후, int 타입 컬럼은 double 타입 컬럼으로 바뀐다.
         * 왜냐하면, mean과 std가 double이기 때문이다.
         *
         * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
         */
        public boolean standardize() {
            boolean check = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).standardize()) check = true;
            }
            return check;
        }

        /**
         * 최솟값 0, 최댓값 1인 컬럼으로 바꾼다. (null은 연산 후에도 null로 유지된다.즉, null은 연산 제외)
         * String 타입 컬럼들에는 영향을 끼치지 않는다.
         * double 혹은 int 타입 컬럼들에 한해서 수행된다.
         * 이 연산 후, int 타입 컬럼은 double 타입 컬럼으로 바뀐다.
         * 왜냐하면, 0과 1사이의 값들은 double이기 때문이다.
         *
         * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
         */
        public boolean normalize() {
            boolean check = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).normalize()) check = true;
            }
            return check;
        }

        /**
         * null을 제외하고 2가지 값으로만 구성된 컬럼이기만 하면 수행된다.
         * 연산 후 0과 1로 구성된 컬럼으로 바뀐다. (null은 연산 후에도 null로 유지된다.즉, null은 연산 제외)
         * 모든 타입 컬럼들에 대해서 수행될 수 있다.
         * 이 연산이 수행된 컬럼은 int 타입 컬럼으로 바뀐다.
         * 왜냐하면, 0과 1이 int이기 때문이다.
         *
         * @return 이 연산에 의해 값이 바뀐 열이 1개라도 발생했다면, true 반환
         */
        public boolean factorize() {
            boolean check = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i).factorize()) check = true;
            }
            return check;
        }

        public String toString() {
            String show;

            show = "<" + getClass().getInterfaces()[0].getName() + "@" + Integer.toHexString(hashCode()) + ">" + "\n";
            show += "RagneIndex: " + getRowCount() + " entries," + "0 to " + (getRowCount() - 1) + "\n";
            show += "Data columns (total " + getColumnCount() + " columns):\n";


            //공백문자 계산을 위한 기초 단계
            int[] space = new int[4];
            String[] header = {"#", "Column", "Non-Null Count", "Dtype"};
            for (int j = 0; j < header.length; j++) {
                space[j] = header[j].length();
            }

            //1열 2열 공백 문자 개수 계산
            for (int i = 0; i < columns.size(); i++) {
                if (space[0] < Integer.toString(i).length())
                    space[0] = Integer.toString(i).length();
                if (space[1] < columns.get(i).getHeader().length())
                    space[1] = columns.get(i).getHeader().length();
            }

            //첫 라인
            String temp = " ";
            show += temp + header[0] + " |";
            show += temp.repeat(space[1] - header[1].length());
            show += header[1] + " |";
            show += temp.repeat(space[2] - header[2].length());
            show += header[2] + " |Dtype\n";

            //type을 위한 계산
            int[] dtypes = new int[3];


            //본문 내용 구성
            for (int i = 0; i < columns.size(); i++) {
                show += temp.repeat(space[0] - Integer.toString(i).length());
                show += i + " |";
                show += temp.repeat(space[1] - columns.get(i).getHeader().length());
                show += columns.get(i).getHeader() + " |";
                String a = Long.toString(getRowCount() - columns.get(i).getNullCount());
                a += " non-null";
                show += temp.repeat(space[2] - a.length());
                show += a + " |";
                show += getColumnType(i) + "\n";
                switch (getColumnType(i)) {
                    case "double":
                        dtypes[0]++;
                        break;
                    case "int":
                        dtypes[1]++;
                        break;
                    case "String":
                        dtypes[2]++;
                        break;
                }
            }

            show += "dtypes: ";
            for (int i = 0; i < dtypes.length; i++) {
                switch (i) {
                    case 0:
                        show += "double(" + dtypes[i] + "), ";
                        break;
                    case 1:
                        show += "int(" + dtypes[i] + "), ";
                        break;
                    case 2:
                        show += "String(" + dtypes[i] + ")\n";
                        break;
                }
            }

            return show;
        }

        private String getColumnType(int index) {
            String Type;

            for (int j = 1; j < columns.get(index).count(); j++) {
                for (int k = 0; k < columns.get(index).getValue(j).length(); k++) {
                    if (columns.get(index).getValue(j).equals("null")) continue;
                    char comp = columns.get(index).getValue(j).charAt(k);
                    if ((comp >= 'a' && comp <= 'z') || (comp <= 'Z' && comp >= 'A')) {
                        Type = "String";
                        return Type;
                    }
                    if (comp == '.') {
                        Type = "double";
                        return Type;
                    }
                }
            }
            Type = "int";
            return Type;
        }

        public boolean equals(Object obj) {
            Table comp = (Table) obj;
            try {
                for (int i = 0; i < columns.size(); i++) {
                    if (!columns.get(i).getHeader().equals(comp.getColumn(i).getHeader()))
                        return false;
                }
                for (int i = 0; i < columns.size(); i++) {
                    for (int j = 1; j < getRowCount(); j++) {
                        if (!columns.get(i).getValue(j).equals(comp.getColumn(i).getValue(j)))
                            return false;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
            return true;
        }

    }

