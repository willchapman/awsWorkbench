package com.raxware.awsworkbench.ui.dialogs;

import com.raxware.awsworkbench.ui.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.control.ButtonType;

/**
 * Provide an object for settings for dialogs that the app wants to use
 * <p>
 * Created by will on 4/7/2016.
 */
public class DialogSettings {
    private String headerText;
    private double x;
    private double y;
    private double width;
    private double height;
    private ObservableList<ButtonType> buttonTypeList = FXCollections.observableArrayList();

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void addButton(ButtonType buttonType) {
        if (buttonType != null)
            buttonTypeList.add(buttonType);
    }

    /**
     * Set the x,y,height and width parameters based on the bounds and the percentage of
     * the area.
     * <p>
     * Note that this will reset the x,y,width and height fields.
     *
     * @param bounds     The bounds to use in the calculation
     * @param percentage The percentage of the bounds to use.
     */
    public void calculate(Bounds bounds, int percentage) {
        if (bounds == null) return;

        double percentVal = Utils.boundsCheck(1, 100, percentage) / 100.0;
        double width = bounds.getWidth() * percentVal;
        double x = (bounds.getWidth() - width) / 2.0;

        double height = bounds.getHeight() * percentVal;
        double y = (bounds.getHeight() - height) / 2.0;

        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    public ObservableList<ButtonType> getButtonTypes() {
        return buttonTypeList;
    }
}
