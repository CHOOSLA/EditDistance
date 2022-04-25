package com.example.editdistance;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private Pane mainPane;

    @FXML
    private TextField str1;

    @FXML
    private TextField str2;

    @FXML
    private Button makeBtn,circularBtn,resetBtn;

    @FXML
    private TextArea historyTextField;

    TableView tableView = new TableView();
    GridPane gridPane = new GridPane();

    final int COPY = 0;
    final int SUBSTITUTE = 1;
    final int INSERT = 2;
    final int DELETE = 3;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gridPane.setMinHeight(531);
        gridPane.setMinWidth(567);
        gridPane.setMaxHeight(531);
        gridPane.setMaxWidth(567);
        mainPane.setBorder(new Border(new BorderStroke(Color.DARKGREY, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(5))));

    }


    @FXML
    protected void makeTable(){
        //버튼 활성화 및 비활성화
        makeBtn.setDisable(true);
        circularBtn.setDisable(false);


        String  s = " ε" +  str1.getText();
        String  t = " ε" + str2.getText();


        double perWidth = 100.0 / t.length();
        double perHeight = 100.0 / s.length();

        for (int i = 0; i < t.length(); i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(perWidth);
            gridPane.getColumnConstraints().add(col);
        }

        for (int i = 0; i < s.length(); i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(perHeight);
            gridPane.getRowConstraints().add(row);
        }

        for (int i = 0; i < s.length(); i++) {
            Label btn = null;


            for (int j = 0; j < t.length(); j++) {
                //인덱스 확인
                //Label btn = new Label(i + "," + j);


                //초기화 부분
                if(i==0){
                    btn = new Label(t.charAt(j) + "");
                }else{
                    btn = new Label("???");
                }
                if(j==0){
                    btn = new Label(s.charAt(i) + "");
                }
                if(i==1&&j>=1){
                    btn = new Label((j-1) +  "");
                }
                if(j==1&&i>=1){
                    btn = new Label((i-1) +  "");
                }

                //스타일 설정 및 삽입
                btn.setFont(new Font("Arial",20));
                StackPane p = new StackPane();
                p.getChildren().add(btn);
                StackPane.setAlignment(btn, Pos.CENTER);

                //각 열마다 색상 지정정
               if(i==0){
                    p.setStyle("-fx-background-color: GREY");
                }else if(j==0){
                    p.setStyle("-fx-background-color: GREY");
                }else{
                    p.setStyle("-fx-background-color: WHITE");
                }

                //각 셀마다 테두리 지정
                p.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                        CornerRadii.EMPTY, new BorderWidths(1))));

                gridPane.add(p, j, i);
            }
        }

        //초기화가 완료된 후 mainPane에 삽입
        mainPane.getChildren().addAll(gridPane);
    }

    @FXML
    protected void editdistance(){
        circularBtn.setDisable(true);

        String  s = " ε" +  str1.getText();
        String  t = " ε" + str2.getText();
        for (int i = 2; i < s.length(); i++) {
            for (int j = 2; j < t.length(); j++) {

                //현재 노드 가져오기
                Node tmp = getNodeFromGridPane(gridPane,j,i);
                Label now = null;
                if(tmp!=null){
                    now = ((Label)((StackPane) tmp).getChildren().get(0));
                }



                tmp = getNodeFromGridPane(gridPane,j -1,i -1);
                int subsitute = Integer.MAX_VALUE;
                if(s.charAt(i) == t.charAt(j)){
                    subsitute = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText());
                }else{
                    subsitute = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText()) + 1;
                }


                //i-i,j 노드 ( 삭제 )
                tmp = getNodeFromGridPane(gridPane,j -1,i);
                int delete = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText()) + 1;
                //i,j-1 노드 ( 삽입 )
                tmp = getNodeFromGridPane(gridPane,j,i -1);
                int insert = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText()) + 1;

                int min = Math.min(subsitute,delete);
                min = Math.min(min,insert);

                now.setText(min +"");
            }
        }

        ArrayList<Integer> history = new ArrayList<>();
        tracking(history,s.length()-1,t.length()-1);
        printHistory(history);

        /*
        //경로 추적
        for (int i = 2; i < s.length(); i++) {
            int min = Integer.MAX_VALUE;

            int tmpj = 0;
            for (int j = 2; j < t.length(); j++) {
                //현재 노드 가져오기
                Node tmp = getNodeFromGridPane(gridPane,j,i);
                int op = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText());
                if(min>=op){
                    min = op;
                    tmpj = j;
                }
            }

            Node tmp = getNodeFromGridPane(gridPane,tmpj,i);
            StackPane minPane = (StackPane) tmp;
            minPane.setStyle("-fx-background-color: GREEN");
        }
         */


        /*
        //테스트 케이스
        Node test = getNodeFromGridPane(gridPane,-1,-1);
        if(test!=null){
            Label tmp = ((Label)((StackPane) test).getChildren().get(0));
            tmp.setText("성공");
        }

         */

    }

    @FXML
    protected void initial(){
        makeBtn.setDisable(false);

        gridPane.getChildren().clear();
        GridPane newGridPane = new GridPane();
        newGridPane.setMinHeight(531);
        newGridPane.setMinWidth(567);
        newGridPane.setMaxHeight(531);
        newGridPane.setMaxWidth(567);
        gridPane = newGridPane;

        historyTextField.setText("");
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void tracking(ArrayList<Integer> history, int lasti, int lastj){
        int i = lasti;
        int j = lastj;


        //우선순위는 대각선 먼저이다
        //i,j의 위치가 각각 1,1일때 멈춘다
        while((i+j)!=2){

            //현재 노드 스타일 설정
            Node tmp = getNodeFromGridPane(gridPane,j,i);
            StackPane minPane = (StackPane) tmp;
            minPane.setStyle("-fx-background-color: GREEN");

            int now = Integer.parseInt(((Label)minPane.getChildren().get(0)).getText());


            //대각선 값
            int diagonal = Integer.MAX_VALUE;
            int left = Integer.MAX_VALUE;
            int top = Integer.MAX_VALUE;

            //i-1,j-1 노드 ( 대체 )
            if((i-1)>=1 && (j-1)>=1){
                tmp = getNodeFromGridPane(gridPane,j-1,i-1);
                diagonal = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText());
            }

            if((j-1)>=1){
                //왼쪽
                tmp = getNodeFromGridPane(gridPane,j-1,i);
                left = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText());
            }

            if((i-1)>=1){
                //위쪽
                tmp = getNodeFromGridPane(gridPane,j,i-1);
                top = Integer.parseInt(((Label)((StackPane) tmp).getChildren().get(0)).getText());
            }




            if(diagonal <= left && diagonal <= top){
                if(diagonal==now){
                    history.add(COPY);
                }else{
                    history.add(SUBSTITUTE);
                }
                i--;
                j--;
            }else if(left <= diagonal && left <= top){
                history.add(INSERT);
                j--;
            }else if(top <= diagonal && top <= left){
                history.add(DELETE);
                i--;
            }
        }

        Collections.reverse(history);
    }

    void printHistory(ArrayList<Integer> history){
        //여기서 1부터 시작하는 이유는
        //첫 번째 연산은 ε , ε 이 두개의 연산이기 때문이다.

        historyTextField.appendText("EDIT DISTANCE FROM '" + str1.getText() + "' to '" + str2.getText() + "'\n\n");

        for (int i = 0; i < history.size(); i++) {
            switch(history.get(i)){
                case COPY:
                    historyTextField.appendText("COPY, ");
                    break;

                case SUBSTITUTE:
                    historyTextField.appendText("SUBSTITUTE, ");
                    break;

                case INSERT:
                    historyTextField.appendText("INSERT, ");
                    break;

                case DELETE:
                    historyTextField.appendText("DELETE, ");
                    break;
            }
        }
    }
}