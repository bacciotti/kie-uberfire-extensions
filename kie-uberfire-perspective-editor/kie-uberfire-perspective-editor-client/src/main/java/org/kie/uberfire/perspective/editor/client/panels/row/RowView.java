package org.kie.uberfire.perspective.editor.client.panels.row;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.kie.uberfire.perspective.editor.model.ColumnEditor;
import org.kie.uberfire.perspective.editor.model.RowEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.client.panels.components.ScreenView;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DropColumnPanel;
import org.kie.uberfire.perspective.editor.client.structure.EditorWidget;
import org.kie.uberfire.perspective.editor.client.structure.PerspectiveEditor;

public class RowView extends Composite {

    private org.kie.uberfire.perspective.editor.client.structure.RowEditor row;

    @UiField
    FluidContainer fluidContainer;

    private EditorWidget editorWidget;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, RowView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public RowView( PerspectiveEditor parent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new org.kie.uberfire.perspective.editor.client.structure.RowEditor( parent, fluidContainer, "12" );
        build();
    }

    public RowView( PerspectiveEditor parent,
                    String rowSpamString ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new org.kie.uberfire.perspective.editor.client.structure.RowEditor( parent, fluidContainer, rowSpamString );
        build();
    }

    public RowView( org.kie.uberfire.perspective.editor.client.structure.ColumnEditor parent,
                    String rowSpamString ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new org.kie.uberfire.perspective.editor.client.structure.RowEditor( parent, fluidContainer, rowSpamString );
        build();

    }

    public RowView( PerspectiveEditor parent,
                    RowEditor rowEditor ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new org.kie.uberfire.perspective.editor.client.structure.RowEditor( parent, fluidContainer, rowEditor.getRowSpam() );
        reload( rowEditor.getColumnEditorsJSON() );
    }

    private RowView( org.kie.uberfire.perspective.editor.client.structure.ColumnEditor parent,
                     List<String> rowSpans ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.editorWidget = parent;
        this.row = new org.kie.uberfire.perspective.editor.client.structure.RowEditor( parent, fluidContainer, rowSpans );
        build();

    }

    private FluidRow generateColumns( List<ColumnEditor> columnEditors ) {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );

        for ( ColumnEditor columnEditor : columnEditors ) {
            Column column = createColumn( columnEditor.getSpan() );
            org.kie.uberfire.perspective.editor.client.structure.ColumnEditor parent = new org.kie.uberfire.perspective.editor.client.structure.ColumnEditor( row, column, columnEditor.getSpan() );
            for ( RowEditor editor : columnEditor.getRows() ) {
                column.add( new RowView( parent, editor.getRowSpam() ) );
            }
            for ( ScreenEditor editor : columnEditor.getScreens() ) {
                //ederign bug puting a drop panel
                column.add( new ScreenView( parent, editor.getParameters() ) );
            }
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void reload( List<ColumnEditor> columnEditors ) {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns( columnEditors ) );
    }

    private FluidRow generateColumns() {
        FluidRow rowWidget = new FluidRow();
        rowWidget.getElement().getStyle().setProperty( "marginBottom", "15px" );
        for ( String span : row.getRowSpans() ) {
            Column column = createColumn( span );
            rowWidget.add( column );
        }
        return rowWidget;
    }

    private void build() {
        row.getWidget().add( generateHeaderRow() );
        row.getWidget().add( generateColumns() );
    }

    private Column createColumn( String span ) {
        Column column = new Column( Integer.valueOf( span ) );
        column.add( generateLabel( "Column" ) );
        org.kie.uberfire.perspective.editor.client.structure.ColumnEditor columnEditor = new org.kie.uberfire.perspective.editor.client.structure.ColumnEditor( row, column, span );
        column.add( new DropColumnPanel( columnEditor ) );
        setCSS( column );
        return column;
    }

    private void setCSS( Column column ) {
        column.getElement().getStyle().setProperty( "border", "1px solid #DDDDDD" );
        column.getElement().getStyle().setProperty( "backgroundColor", "White" );
    }

    private FluidRow generateHeaderRow() {
        FluidRow row = new FluidRow();
        row.add( generateRowLabelColumn() );
        row.add( generateButtonColumn() );
        return row;
    }

    private Column generateRowLabelColumn() {
        Column column = new Column( 6 );
        Label row1 = generateLabel( "Row" );
        column.add( row1 );
        return column;
    }

    private Column generateButtonColumn() {
        Column buttonColumn = new Column( 6 );
        buttonColumn.getElement().getStyle().setProperty( "textAlign", "right" );
        Button remove = generateButton();
        buttonColumn.add( remove );
        return buttonColumn;
    }

    private Button generateButton() {
        Button remove = new Button( "Remove" );
        remove.setSize( ButtonSize.MINI );
        remove.setType( ButtonType.DANGER );
        remove.getElement().getStyle().setProperty( "marginRight", "3px" );
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                editorWidget.getWidget().remove( RowView.this );
                row.removeFromParent();
            }
        } );
        return remove;
    }

    private Label generateLabel( String row ) {
        Label label = new Label( row );
        label.getElement().getStyle().setProperty( "marginLeft", "3px" );
        return label;
    }

}
