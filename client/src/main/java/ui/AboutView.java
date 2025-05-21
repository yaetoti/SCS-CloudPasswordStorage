package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class AboutView {
  @FXML
  public Pane bottomPane;
  @FXML
  public TextFlow textFlow;
  @FXML
  BorderPane borderPane;

  public AboutView() {

  }

  @FXML
  private void initialize() {
    String[] strings = {
      """
      Застосунок для демонстрації виконання комплексної лабораторної роботи з предмету "Спеціалізовані комп'ютерні системи"
      """,
      """
      
      Індивідуальне завдання:
      """,
      """
      - Створити комплекс програм для прийому та передачі критичної інформації (паролів, ключів, тощо) зі збереженням її на носії
      - Реалізувати інтерфейс користувача
      - Інтерфейс користувача повинен містити засоби парольної ідентифікації
      """,
      """
      
      Використані алгоритми:
      """,
      """
      - Шифрування подвійною перестановкою
      - Шифр Цезаря (шифр підстановки)
      - Конфігурація Галуа (XOR шифр)
      - Архівація GIF LZW (архівація послідовності бітів з використанням словника)
      - Система авторизації з використанням токенів на основі цифрового підпису асиметричним алгоритмом RSA
      - Знищення інформації на диску алгоритмом Брюса Шнайєра (7 проходів перезапису)
      """,
      """
      
      
      Автор: Костін А.С.
      Група: КН-Н924в
      """
    };

    Text[] texts = {
      new Text(strings[0]),
      new Text(strings[1]),
      new Text(strings[2]),
      new Text(strings[3]),
      new Text(strings[4]),
      new Text(strings[5]),
    };

    texts[0].setStyle("-fx-font-weight: 900; -fx-font-size: 16pt;");
    texts[1].setStyle("-fx-font-weight: 700; -fx-font-size: 14pt;");
    texts[2].setStyle("-fx-font-weight: 200; -fx-font-size: 14pt;");
    texts[3].setStyle("-fx-font-weight: 700; -fx-font-size: 14pt;");
    texts[4].setStyle("-fx-font-weight: 200; -fx-font-size: 14pt;");
    texts[5].setStyle("-fx-font-weight: 700; -fx-font-style: italic; -fx-font-size: 14pt;");

    textFlow.getChildren().addAll(texts);

    borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
      textFlow.setPrefWidth(newValue.doubleValue());
    });

    borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
      textFlow.setPrefHeight(newValue.doubleValue());
    });
  }
}
