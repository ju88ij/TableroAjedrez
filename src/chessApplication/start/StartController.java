package chessApplication.start;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class StartController {

	private ObservableBooleanValue accepted   = new SimpleBooleanProperty(false);
	private String playerWhite;
	private String playerBlack;
	
	@FXML VBox              root;
	@FXML Button            cancelButton;
	@FXML Button            acceptButton;
	@FXML Label             hoursLabel;
	@FXML Label             minutesLabel;
	@FXML TextField         hoursField;
	@FXML TextField         minutesField;
	@FXML TextField         player1Field;
	@FXML TextField         player2Field;
	@FXML CheckBox          time;
	@FXML ChoiceBox<String> colorPlayer1;
	@FXML ChoiceBox<String> colorPlayer2;
	@FXML ChoiceBox<String> colorPiecesAtBottom;
	
	
	//METHODS
	/*
	 * this method is automatically called by the FXML.Loader.
	 * textFormatters can only be used in one TextInputControl at a time.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	private void initialize() {
		//minutes can be 1-59 or nothing.
		Pattern minutesPattern = Pattern.compile("[1-9]|[0-5][0-9]|\\A\\Z");
		//hours can be 0-10 or nothing.
		Pattern hoursPattern = Pattern.compile("[0-9]|[0-1][0]|\\A\\Z");
		//players names can be numbers, letters,_, or -.
		Pattern playersPattern = Pattern.compile("^[A-Za-z0-9_-]{0,11}$");
		TextFormatter minutesFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
			return minutesPattern.matcher(change.getControlNewText()).matches() ? change : null;
		});
		TextFormatter hoursFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) 	 change -> {
			return hoursPattern.matcher(change.getControlNewText()).matches() ? change : null;
		});
		TextFormatter player1Formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
			return playersPattern.matcher(change.getControlNewText()).matches() ? change : null;
		});
		TextFormatter player2Formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change -> {
			return playersPattern.matcher(change.getControlNewText()).matches() ? change : null;
		});
		hoursField.setTextFormatter(hoursFormatter);
		minutesField.setTextFormatter(minutesFormatter);
		player1Field.setTextFormatter(player1Formatter);
		player2Field.setTextFormatter(player2Formatter);
	}

	@FXML
	private void cancelButtonAction(ActionEvent event) {
		((Stage) cancelButton.getScene().getWindow()).close();;
	}

	@FXML
	private void acceptButtonAction(ActionEvent event) {
		((SimpleBooleanProperty) accepted).set(false);
		//force user to give names and colors.
		if (player1Field.getText().trim().equals("")) player1Field.requestFocus();
		else if (player2Field.getText().trim().equals("")) player2Field.requestFocus();
		else if (colorPlayer1.getSelectionModel().getSelectedItem() == null) colorPlayer1.requestFocus();
		else if (colorPiecesAtBottom.getSelectionModel().getSelectedItem() == null) colorPiecesAtBottom.requestFocus();
		else {
			playerWhite = colorPlayer1.getSelectionModel().getSelectedItem().equals("White") ? player1Field.getText() : player2Field.getText();
			playerBlack = colorPlayer1.getSelectionModel().getSelectedItem().equals("White") ? player2Field.getText() : player1Field.getText();
			hoursField.setText(hoursField.getText().trim().equals("") || !time.isSelected() ? "00" :  hoursField.getText());
			minutesField.setText(minutesField.getText().trim().equals("") || !time.isSelected() ? "00" : minutesField.getText());
			((SimpleBooleanProperty) accepted).set(true);
			((Stage) acceptButton.getScene().getWindow()).close();
		}
	}

	//if one player chooses color White, choiceBox for other player is set to Black and vice versa.
	@SuppressWarnings (value="unchecked")
	@FXML
	private void colorChoiceBoxAction(ActionEvent event) {
		ChoiceBox<String> activeBox = (ChoiceBox<String>) event.getSource();
		ChoiceBox<String> otherBox  = activeBox == colorPlayer1 ? colorPlayer2 : colorPlayer1;
		otherBox.getSelectionModel().select(activeBox.getSelectionModel().getSelectedIndex() == 0 ? 1 : 0);
	}
	
	//GETTERS & SETTERS
	public ObservableBooleanValue getAccepted() {
		return accepted;
	}

	public String getPlayerWhite() {
		return playerWhite;
	}

	public String getPlayerBlack() {
		return playerBlack;
	}
	
	public int getHours() {
		return Integer.parseInt(hoursField.getText());
	}
	
	public int getMinutes() {
		return Integer.parseInt(minutesField.getText());
	}
}
