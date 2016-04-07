package com.raxware.awsworkbench.model.s3;

import com.raxware.awsworkbench.res.Resources;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Formats the TableCell filled with S3KyEntry objects
 *
 * Created by will on 3/25/2016.
 */
public class S3KeyTableCell extends TableCell<S3KeyEntry,S3KeyEntry> {

    private Fields field;

    private S3KeyTableCell() {super();}

    public static S3KeyTableCell makeName() {
        return build(Fields.NAME);
    }

    public static S3KeyTableCell makeLastModified() {
        return build(Fields.LAST_MODIFIED);
    }

    public static S3KeyTableCell makeSize() {
        return build(Fields.SIZE);
    }

    private static S3KeyTableCell build(Fields type) {
        S3KeyTableCell cell = new S3KeyTableCell();
        cell.field = type;
        return cell;
    }


    @Override
    protected void updateItem(S3KeyEntry item, boolean empty) {
        super.updateItem(item, empty);

        if(item == null || empty) {
            setText("");
            setGraphic(null);
            return;
        }

        switch(field) {
            case NAME:
                updateName(item);
                break;
            case LAST_MODIFIED:
                updateLastModified(item);
                break;
            case SIZE:
                updateSize(item);
                break;
            default:
                System.out.println("Unknown field");
                return;
        }
    }

    private void updateSize(S3KeyEntry item) {
        setText(item.getSizeDisplay());
    }

    private void updateLastModified(S3KeyEntry item) {
        if(item.getLastModifiedRaw() == 0)
            setText("");
        else
            setText(item.getLastModified().toString());
    }

    private void updateName(S3KeyEntry item) {

        HBox box = new HBox();
        Image icon = item.isDirectory() ? getDirectoryIcon() : getFileIcon();
        ImageView view = new ImageView(icon);
        Label nameLabel = new Label(item.getDisplayName(true));
        box.getChildren().addAll(view, nameLabel);
        setGraphic(box);
    }

    private Image getDirectoryIcon() {
        return Resources.getPngIcon("folder", "20");
    }

    private Image getFileIcon() {
        return Resources.getPngIcon("file", "20");
    }

    public enum Fields {
        NAME, LAST_MODIFIED, SIZE
    }
}
