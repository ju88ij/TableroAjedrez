//Yasin
package chessApplication.promotion;

import static javafx.scene.layout.BorderStrokeStyle.SOLID;
import static javafx.scene.layout.CornerRadii.EMPTY;
import static javafx.scene.paint.Color.DARKGOLDENROD;
import static javafx.scene.paint.Color.TRANSPARENT;

import java.util.List;
import java.util.stream.Collectors;

import chessApplication.SecondStage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PromotionController {

	public StringProperty piece           = new SimpleStringProperty("");
	private Border        borderInvisible = new Border (new BorderStroke(TRANSPARENT, SOLID, EMPTY, null));
	private Border        borderVisible   = new Border (new BorderStroke(DARKGOLDENROD, SOLID, EMPTY, null));
	
	@FXML VBox   root;
	@FXML HBox   pieces;
	@FXML Button selectButton;
	
	
	//METHODS
	@FXML
	private void initialize() {
		pieces.getChildren().forEach(c -> {
			((Labeled) c).setBorder(borderInvisible);
		});
	}

	@FXML
	private void selectPiece(MouseEvent event) {
		pieces.getChildren().forEach(c -> {
			((Labeled) c).setBorder(borderInvisible);
		});
		((Label) event.getSource()).setBorder(borderVisible);
	}
	
	@FXML
	private void selectButtonAction(ActionEvent event) {
		((SimpleStringProperty) piece).set("");
		List<Node> selectedPiece = pieces.getChildren()
				.stream()
				.filter(c -> ((Labeled) c).getBorder() == borderVisible)
				.collect(Collectors.toList());
		if (! selectedPiece.isEmpty()) {
			piece.set(((Labeled) selectedPiece.get(0)).getText());
			((SecondStage) selectButton.getScene().getWindow()).setPromotionPieceSelected(true);
			((SecondStage) selectButton.getScene().getWindow()).close();
		}
		
	}
}
