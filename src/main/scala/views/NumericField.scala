package views

import javafx.beans.value.{ChangeListener, ObservableValue}
import scalafx.scene.control.TextField

class NumericField(maxValue: Int) extends TextField(
  new javafx.scene.control.TextField {
    textProperty().addListener(new NumericFieldChangeListener(this))

    private class NumericFieldChangeListener(textField: javafx.scene.control.TextField) extends ChangeListener[String] {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        if (!newValue.matches("\\d{0,3}"))
          textField.setText(oldValue)
        if(textField.getText != "" && textField.getText.toInt > maxValue)
          textField.setText(maxValue.toString)
      }
    }
  })
