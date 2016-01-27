package org.roger600.lienzo.client;

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.shape.wires.*;
import com.ait.lienzo.client.core.shape.wires.event.*;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;


public class WiresTests extends FlowPanel {
    
    private Layer layer;
    private IControlHandleList m_ctrls;
    private WiresShape startEventShape;
    private Circle startEventCircle;

    public WiresTests(Layer layer) {
        this.layer = layer;
    }

    public void testWires() {
        WiresManager wires_manager = WiresManager.get(layer);

        wires_manager.setConnectionAcceptor(new IConnectionAcceptor() {
            @Override
            public boolean acceptHead(WiresConnection head, WiresMagnet magnet) {
                WiresConnection tail = head.getConnector().getTailConnection();

                WiresMagnet m = tail.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(magnet.getMagnets().getGroup(), tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean acceptTail(WiresConnection tail, WiresMagnet magnet) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(), magnet.getMagnets().getGroup());
            }

            @Override
            public boolean headConnectionAllowed(WiresConnection head, WiresShape shape) {
                WiresConnection tail = head.getConnector().getTailConnection();
                WiresMagnet m = tail.getMagnet();

                if (m == null)
                {
                    return true;
                }

                return accept(shape.getGroup(), tail.getMagnet().getMagnets().getGroup());
            }

            @Override
            public boolean tailConnectionAllowed(WiresConnection tail, WiresShape shape) {
                WiresConnection head = tail.getConnector().getHeadConnection();

                WiresMagnet m = head.getMagnet();

                if (m == null)
                {
                    return true;
                }
                return accept(head.getMagnet().getMagnets().getGroup(), shape.getGroup());
            }

            private boolean accept(final Group head, final Group tail)
            {
                log("Accept [head=" + head.getUserData() + "] [tail=" + tail.getUserData() + "]");
                final String headData = (String) head.getUserData();
                final String tailData = (String) tail.getUserData();
                if ( "event".equals(headData) && "event".equals(tailData) )
                {
                    return false;
                }
                return true;
            }
        });

        wires_manager.setContainmentAcceptor(new IContainmentAcceptor()
        {
            @Override
            public boolean containmentAllowed(WiresContainer parent, WiresShape child)
            {
                return acceptContainment(parent, child);
            }

            @Override
            public boolean acceptContainment(WiresContainer parent, WiresShape child)
            {
                if (parent.getParent() == null)
                {
                    return true;
                }
                return !parent.getContainer().getUserData().equals(child.getGroup().getUserData());
            }
        });

        final double startX = 300;
        final double startY = 300;
        final double radius = 50;
        final double w = 100;
        final double h = 100;

        // Blue start event.
        MultiPath startEventMultiPath = new MultiPath().rect(0, 0, w, h).setStrokeColor("#000000");
        startEventShape = wires_manager.createShape(startEventMultiPath);
        startEventCircle = new Circle(radius).setFillColor("#0000CC").setDraggable(false);
        startEventShape.getGroup().setX(startX).setY(startY).setUserData("event");
        startEventShape.addChild(startEventCircle, WiresLayoutContainer.Layout.TOP);
        // startEventShape.addChild(new Rectangle(50, 50).setX(0).setY(0).setFillColor(ColorName.BLACK), WiresPrimitivesContainer.Layout.LEFT);
        // ( (WiresLayoutContainer) startEventShape.getGroup()).add(startEventCircle, WiresLayoutContainer.Layout.CENTER);

        // Green task node.
        WiresShape taskNodeShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setFillColor("#00CC00"));
        taskNodeShape.getGroup().setX(startX + 200).setY(startY).setUserData("task");

        // Yellow task node.
        WiresShape task2NodeShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setFillColor("#FFEB52"));
        task2NodeShape.getGroup().setX(startX + 200).setY(startY + 300).setUserData("task");

        // Red end event.
        WiresShape endEventShape = wires_manager.createShape(new MultiPath().rect(0, 0, w, h).setStrokeColor("#FFFFFF"));
        endEventShape.getGroup().setX(startX + 400).setY(startY).add(new Circle(radius).setX(50).setY(50).setFillColor("#CC0000").setDraggable(false));
        endEventShape.getGroup().setUserData("event");

        // Connector from blue start event to green task node.
        connect(layer, startEventShape.getMagnets(), 3, taskNodeShape.getMagnets(), 7, wires_manager, true, false);
        // Connector from green task node to red end event 
        connect(layer, taskNodeShape.getMagnets(), 3, endEventShape.getMagnets(), 7, wires_manager, true, false);
        // Connector from blue start event to yellow task node.
        connect(layer, startEventShape.getMagnets(), 3, task2NodeShape.getMagnets(), 7, wires_manager, true, false);

        startEventShape.setDraggable(true).addWiresHandler(AbstractWiresEvent.DRAG, new DragHandler() {
            @Override
            public void onDragStart(DragEvent dragEvent) {
                final WiresShape shape = dragEvent.getShape();
                final double dx = dragEvent.getX();
                final double dy = dragEvent.getY();
                log("onDragStart#DRAG - [shape=" + shape + ", x=" + dx + ", y=" + dy + "]");
            }

            @Override
            public void onDragMove(DragEvent dragEvent) {
                final WiresShape shape = dragEvent.getShape();
                final double dx = dragEvent.getX();
                final double dy = dragEvent.getY();
                log("onDragMove#DRAG - [shape=" + shape + ", x=" + dx + ", y=" + dy + "]");
            }

            @Override
            public void onDragEnd(DragEvent dragEvent) {
                final WiresShape shape = dragEvent.getShape();
                final double dx = dragEvent.getX();
                final double dy = dragEvent.getY();
                log("onDragEnd#DRAG - [shape=" + shape + ", x=" + dx + ", y=" + dy + "]");
            }
        });

        startEventShape.setResizable(true).addWiresHandler(AbstractWiresEvent.RESIZE, new ResizeHandler() {

            private int sx;
            private double sr;
            
            @Override
            public void onResizeStart(final ResizeEvent resizeEvent) {
                final WiresShape shape = resizeEvent.getShape();
                final int index = resizeEvent.getControlIndex();
                final IPrimitive<?> control = shape.getControls().getHandle(index).getControl();
                final int rx = resizeEvent.getX();
                final int ry = resizeEvent.getY();
                this.sx = rx;
                this.sr = startEventCircle.getRadius();
                log("onResizeStart#RESIZE - [shape=" + shape  + ", x=" + rx + ", y=" + ry + ", control=" + control + "]");
            }

            @Override
            public void onResizeStep(final ResizeEvent resizeEvent) {
                final WiresShape shape = resizeEvent.getShape();
                final int index = resizeEvent.getControlIndex();
                final IPrimitive<?> control = shape.getControls().getHandle(index).getControl();
                final int rx = resizeEvent.getX();
                final int ry = resizeEvent.getY();
                /*final double radius = ( sr * rx ) / sx;
                log("Circle RADIUS="+radius);
                startEventCircle.setRadius(radius);*/
                log("onResizeStep#RESIZE - [shape=" + shape  + ", x=" + rx + ", y=" + ry + ", control=" + control + "]");
            }

            @Override
            public void onResizeEnd(final ResizeEvent resizeEvent) {
                final WiresShape shape = resizeEvent.getShape();
                final int index = resizeEvent.getControlIndex();
                final IPrimitive<?> control = shape.getControls().getHandle(index).getControl();
                final double rx = resizeEvent.getX();
                final double ry = resizeEvent.getY();
                log("onResizeEnd#RESIZE - [shape=" + shape  + ", x=" + rx + ", y=" + ry + ", control=" + control + "]");
            }
        });

        addButton(layer);
        addButton2(layer);
    }
    
    private double getCircleRadius() {
        final double w = startEventShape.getGroup().getBoundingBox().getWidth();
        return w / 2;
    }
    
    private Rectangle button;
    
    private void addButton(final Layer layer) {
        button = new Rectangle(50, 50).setFillColor(ColorName.BLACK);
        button.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(NodeMouseClickEvent event) {
                
                // startEventShape.getGroup().setX(100).setY(100);
                
                logButton(button);
                button.setScale(2, 2);
                layer.batch();
                logButton(button);
                button.setX(100);
                button.setY(100);
            }
        });
        layer.add(button);
    }

    private void addButton2(final Layer layer) {
        final Rectangle button2 = new Rectangle(50, 50).setX(0).setY(600).setFillColor(ColorName.BLACK);
        button2.addNodeMouseClickHandler(new NodeMouseClickHandler() {
            @Override
            public void onNodeMouseClick(NodeMouseClickEvent event) {
                logButton(button);
                button.setWidth(button.getWidth() + 1);
                button.setHeight(button.getHeight() + 1);
                logButton(button);
            }
        });
        layer.add(button2);
    }
    
    private void logButton(Rectangle button) {
        GWT.log("Button [x=" + button.getX() + ", y=" + button.getY() 
        + ", w=" + button.getWidth() + ", h=" + button.getHeight() + "]");
    }

    private void connect(Layer layer, MagnetManager.Magnets headMagnets, int headMagnetsIndex, MagnetManager.Magnets tailMagnets, int tailMagnetsIndex, WiresManager wires_manager,
                         final boolean tailArrow, final boolean headArrow)
    {
        WiresMagnet m0_1 = headMagnets.getMagnet(headMagnetsIndex);

        WiresMagnet m1_1 = tailMagnets.getMagnet(tailMagnetsIndex);

        double x0 = m0_1.getControl().getX();

        double y0 = m0_1.getControl().getY();

        double x1 = m1_1.getControl().getX();

        double y1 = m1_1.getControl().getY();

        OrthogonalPolyLine line = createLine(x0, y0, (x0 + ((x1 - x0) / 2)), (y0 + ((y1 - y0) / 2)), x1, y1);

        WiresConnector connector = wires_manager.createConnector(m0_1, m1_1, line,
                headArrow ? new SimpleArrow(20, 0.75) : null,
                tailArrow ? new SimpleArrow(20, 0.75) : null);

        connector.getDecoratableLine().setStrokeWidth(5).setStrokeColor("#0000CC");
    }

    private final OrthogonalPolyLine createLine(final double... points)
    {
        return new OrthogonalPolyLine(Point2DArray.fromArrayOfDouble(points)).setCornerRadius(5).setDraggable(true);
    }
    
    private void log(String message) {
        // GWT.log(message);
    }

}
