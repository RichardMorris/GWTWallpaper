/*
Created 20 Feb 2009 - Richard Morris
 */
package org.singsurf.wallpaper;

import java.util.ArrayList;
import java.util.List;

import org.singsurf.wallpaper.tessrules.DiamondRule;
import org.singsurf.wallpaper.tessrules.FrezeRule;
import org.singsurf.wallpaper.tessrules.HexiRule;
import org.singsurf.wallpaper.tessrules.PgramRule;
import org.singsurf.wallpaper.tessrules.PointRule;
import org.singsurf.wallpaper.tessrules.RectRule;
import org.singsurf.wallpaper.tessrules.SquRule;
import org.singsurf.wallpaper.tessrules.TessRule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;


public class GraphicalTesselationPanel extends FlexTable implements ChangeHandler {

    ListBox friezeChoice;
    ListBox cycleChoice;
    ListBox dyhChoice;
    ListBox basicChoice;
    public static final String iconPrefix = "patternIcons/"; 
    public static final String iconSuffix = "S.png"; 
    Controller cont;
    GraphicalTesselationBox currentGTB = null;    
    TessRule currentTr;

    List<GraphicalTesselationBox> allBoxes = new ArrayList<GraphicalTesselationBox>();
    /**
     * 
     */
    public GraphicalTesselationPanel(Controller controller) {
        this.cont = controller;

        // controller = new Controller(this,)
        GraphicalTesselationBox TTcb = new GraphicalTesselationBox(PgramRule.rhombusTT,"p1");
        GraphicalTesselationBox R1cb = new GraphicalTesselationBox(PgramRule.rhombusR1,"p2");
//        GraphicalTesselationBox R1cb = new GraphicalTesselationBox(IrregularHexRule.p2hex,"p2");
        GraphicalTesselationBox CMcb = new GraphicalTesselationBox(DiamondRule.rhombCM,"cm");
        GraphicalTesselationBox CMMcb = new GraphicalTesselationBox(DiamondRule.rhombCMM,"cmm");
        GraphicalTesselationBox PMcb = new GraphicalTesselationBox(RectRule.rectPM,"pm");
        GraphicalTesselationBox PGcb = new GraphicalTesselationBox(RectRule.rectPG,"pg");
        GraphicalTesselationBox PMGcb = new GraphicalTesselationBox(RectRule.rectPMG,"pmg");
        GraphicalTesselationBox PGGcb = new GraphicalTesselationBox(RectRule.rectPGG,"pgg");
        GraphicalTesselationBox PMMcb = new GraphicalTesselationBox(RectRule.rectPMM,"pmm");
        GraphicalTesselationBox P4cb = new GraphicalTesselationBox(SquRule.squP4,"p4");
        GraphicalTesselationBox P4Gcb = new GraphicalTesselationBox(SquRule.squP4g,"p4g");
        GraphicalTesselationBox P4Mcb = new GraphicalTesselationBox(SquRule.squP4m,"p4m");
        GraphicalTesselationBox P3cb = new GraphicalTesselationBox(HexiRule.triP3,"p3");
        GraphicalTesselationBox P31Mcb = new GraphicalTesselationBox(HexiRule.triP31m,"p31m");
        GraphicalTesselationBox P3M1cb = new GraphicalTesselationBox(HexiRule.triP3m1,"p3m1");
        GraphicalTesselationBox P6cb = new GraphicalTesselationBox(HexiRule.triP6,"p6");
        GraphicalTesselationBox P6Mcb = new GraphicalTesselationBox(HexiRule.triP6m,"p6m");
/*
    	this.setWidget(0, 0, TTcb.but);
    	this.setWidget(0, 1, R1cb.but);
    	this.setWidget(1, 0, CMcb.but);
    	this.setWidget(1, 1, CMMcb.but);
    	this.setWidget(2, 0, PMcb.but);
    	this.setWidget(2, 1, PGcb.but);
    	this.setWidget(3, 0, PMGcb.but);
    	this.setWidget(3, 1, PGGcb.but);
    	this.setWidget(4, 0, PMMcb.but);
    	this.setWidget(4, 1, P4cb.but);
    	this.setWidget(5, 0, P4Gcb.but);
    	this.setWidget(5, 1, P4Mcb.but);
    	this.setWidget(6, 0, P3cb.but);
    	this.setWidget(6, 1, P31Mcb.but);
    	this.setWidget(7, 0, P3M1cb.but);	
    	this.setWidget(7, 1, P6cb.but);
    	this.setWidget(8, 0, P6Mcb.but);
*/  	
    	this.setWidget(0, 0, TTcb.but);
    	this.setWidget(0, 1, R1cb.but);
    	this.setWidget(1, 0, CMcb.but);
    	this.setWidget(1, 1, CMMcb.but);
    	
    	this.setWidget(2, 0, PMcb.but);
    	this.setWidget(2, 1, PGcb.but);
    	
    	this.setWidget(3, 0, PMMcb.but);
    	this.setWidget(3, 1, PMGcb.but);
    	this.setWidget(3, 2, PGGcb.but);
    	
    	this.setWidget(4, 0, P4cb.but);
    	this.setWidget(4, 1, P4Mcb.but);
    	this.setWidget(4, 2, P4Gcb.but);
    	
    	this.setWidget(5, 0, P3cb.but);
    	this.setWidget(5, 1, P3M1cb.but);	
    	this.setWidget(5, 2, P31Mcb.but);
    	
    	this.setWidget(6, 0, P6cb.but);
    	this.setWidget(6, 1, P6Mcb.but);

        
        friezeChoice = new ListBox();
        friezeChoice.setVisibleItemCount(1);
        String descript[] = {"Frieze groups"," - pppp"," - pbpb"," - cccc"," - pqpq"," - pdpd"," - pdbq"," - xxxx"};
        friezeChoice.addItem(descript[0]);
        for(int i=1;i<=7;++i) {
            friezeChoice.addItem("F"+i+descript[i]);
        }
        friezeChoice.addChangeHandler(this);
        this.getFlexCellFormatter().setColSpan(9, 0, 3);
        this.setWidget(9,0,friezeChoice);

        cycleChoice = new ListBox();
        cycleChoice.setVisibleItemCount(1);
        cycleChoice.addItem("Cyclic groups");
        for(int i=2;i<11;++i) {
            String label = "C" + i;
            cycleChoice.addItem(label);
        }
        cycleChoice.addChangeHandler(this);
        this.getFlexCellFormatter().setColSpan(10, 0, 3);
        this.setWidget(10,0,cycleChoice);

        dyhChoice = new ListBox();
        dyhChoice.setVisibleItemCount(1);
        dyhChoice.addItem("Dihedral groups");
        for(int i=1;i<11;++i) {
            String label = "D" + i;
            if(i==1) label = label + "- a reflection";
            dyhChoice.addItem(label);
        }
        dyhChoice.addChangeHandler(this);
        this.getFlexCellFormatter().setColSpan(11, 0, 3);
        this.setWidget(11,0,dyhChoice);

        basicChoice = new ListBox();
        basicChoice.setVisibleItemCount(1);
        basicChoice.addItem("Basics transformations");
        for(int i=0;i<TessRule.basicNames.length;++i) {
            basicChoice.addItem(TessRule.basicNames[i]);
        }
        basicChoice.addChangeHandler(this);

        this.getFlexCellFormatter().setColSpan(12, 0, 3);
        this.setWidget(12,0,basicChoice);

        // done layout


        int rand = (int) (Math.random() * 17);
               //rand = 5;
        GraphicalTesselationBox box = null;
        switch (rand) {
        case 0:
            box = TTcb;
            break;
        case 1:
            box = R1cb;
            break;
        case 2:
            box = CMcb;
            break;
        case 3:
            box = CMMcb;
            break;
        case 4:
            box = PMcb;
            break;
        case 5:
            box = PGcb;
            break;
        case 6:
            box = PMGcb;
            break;
        case 7:
            box = PGGcb;
            break;
        case 8:
            box = PMMcb;
            break;
        case 9:
            box = P4cb;
            break;
        case 10:
            box = P4Gcb;
            break;
        case 11:
            box = P4Mcb;
            break;
        case 12:
            box = P3cb;
            break;
        case 13:
            box = P31Mcb;
            break;
        case 14:
            box = P3M1cb;
            break;
        case 15:
            box = P6cb;
            break;
        case 16:
            box = P6Mcb;
            break;
        }
        this.currentTr = box.tr;
        
        currentGTB = box;
        currentGTB.but.setDown(true);
    }

	@Override
	public void onChange(ChangeEvent event) {
    //public void itemStateChanged(ItemEvent e) {
		ListBox selectedList = (ListBox) event.getSource();
		
        //ItemSelectable sel = event.
        int index = selectedList.getSelectedIndex();
        String label = selectedList.getItemText(index);
        currentTr = null;
        if(Character.isDigit(label.charAt(1)))
        {
            int num = Integer.parseInt(label.substring(1,
                    label.length()>2 &&Character.isDigit(label.charAt(2)) ? 3 : 2));
            if(label.startsWith("C")) {
                currentTr = PointRule.cycleRules[num];
                friezeChoice.setSelectedIndex(0);
                dyhChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
            }
            else if(label.startsWith("D")) {
                currentTr = PointRule.dyhRules[num];
                friezeChoice.setSelectedIndex(0);
                cycleChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
            }
            else if(label.startsWith("F")) {
                dyhChoice.setSelectedIndex(0);
                cycleChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);

                switch(num) {
                case 1: currentTr = FrezeRule.F1; break;
                case 2: currentTr = FrezeRule.F2; break;
                case 3: currentTr = FrezeRule.F3; break;
                case 4: currentTr = FrezeRule.F4; break;
                case 5: currentTr = FrezeRule.F5; break;
                case 6: currentTr = FrezeRule.F6; break;
                case 7: currentTr = FrezeRule.F7; break;
                default:
                    return;
                }
            }
        } 
        else
        {
            //friezeChoice.setSelectedIndex(0);
            //cycleChoice.setSelectedIndex(0);
            //dyhChoice.setSelectedIndex(0);

            currentTr = TessRule.getTessRuleByName(label);
            if(currentTr==null)
                return;
        }
        setCurrentBox(null);
        cont.setText(currentTr.message);
        cont.setTesselation(currentTr);
        //                      if(!TessRule.this.wallpaper.accumeMode.getState())
        //                              System.arraycopy(TessRule.this.wallpaper.inpixels,0,TessRule.this.wallpaper.pixels,0,this.wallpaper.inpixels.length);
        //                      TessRule.this.fixVerticies(vertexX,vertexY);
        cont.applyTessellation();
        currentTr.firstCall=false;
        cont.repaint();
    }

    public TessRule getCurrentTesselation() {
        return currentTr;
    }

    public void tickCheckbox(String name) {
        for(GraphicalTesselationBox tb : allBoxes) {
            if(name.equals(tb.getTessName())) {
            	tb.but.setDown(true);
            	setCurrentBox(tb);
                currentTr = tb.tr;
                friezeChoice.setSelectedIndex(0);
                cycleChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
                dyhChoice.setSelectedIndex(0);
                return;
            }
        }
        if(Character.isDigit(name.charAt(1)))
        {
            int num = Integer.parseInt(name.substring(1,
                    name.length()>2 &&Character.isDigit(name.charAt(2)) ? 3 : 2));
            if(name.startsWith("C")) {
                currentTr = PointRule.cycleRules[num];
                cycleChoice.setSelectedIndex(num-1);
                dyhChoice.setSelectedIndex(0);
                friezeChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
            	setCurrentBox(null);
            }
            else if(name.startsWith("D")) {
                currentTr = PointRule.dyhRules[num];
                cycleChoice.setSelectedIndex(0);
                dyhChoice.setSelectedIndex(num);
                friezeChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
            	setCurrentBox(null);
            }
            else if(name.startsWith("F")) {
                dyhChoice.setSelectedIndex(0);
                cycleChoice.setSelectedIndex(0);
                basicChoice.setSelectedIndex(0);
                friezeChoice.setSelectedIndex(num);

                switch(num) {
                case 1: currentTr = FrezeRule.F1; break;
                case 2: currentTr = FrezeRule.F2; break;
                case 3: currentTr = FrezeRule.F3; break;
                case 4: currentTr = FrezeRule.F4; break;
                case 5: currentTr = FrezeRule.F5; break;
                case 6: currentTr = FrezeRule.F6; break;
                case 7: currentTr = FrezeRule.F7; break;
                default:
                    return;
                }
            	setCurrentBox(null);
            }
        }
        else {
            for(int i=1;i<basicChoice.getItemCount();++i)
            {
                if(name.equals(basicChoice.getItemText(i))) {
                    basicChoice.setSelectedIndex(i);
                    currentTr = TessRule.getTessRuleByName(name);
                    dyhChoice.setSelectedIndex(0);
                    cycleChoice.setSelectedIndex(0);
                    friezeChoice.setSelectedIndex(0);
                	setCurrentBox(null);
                }
            }
        }
        return;
    }
    
    void setCurrentBox(GraphicalTesselationBox box) {
    	GWT.log("Current "+currentGTB.name);
    	if(currentGTB != null) {
    		currentGTB.but.setDown(false);
    	}
    	currentGTB = box;
    }

    
    class GraphicalTesselationBox implements ClickHandler {
        TessRule tr;
        Image img;
        ToggleButton but;
        String name;
        public GraphicalTesselationBox(TessRule tr, String iconName) {
        	name = iconName;
            String iconFileName = iconPrefix + iconName + iconSuffix;
    	    img = new Image(iconFileName);
    	    but = new ToggleButton(iconName);
    	    but.setHTML("<img src=\""+iconFileName+"\">"+iconName);

//TODO            this.setToolTipText(iconName);

            this.tr = tr;
            but.addClickHandler(this);
            allBoxes.add(this);
        }

        public Object getTessName() {
            return tr.name;
        }
        
		@Override
		public void onClick(ClickEvent event) {
			GWT.log(name+"click");
//			but.setDown(true);
            currentTr = tr;
            tr.firstCall=true;
            cont.setText(tr.name + ": " + tr.message);
            cont.setTesselation(tr);
            //                  if(!TessRule.this.wallpaper.accumeMode.getState())
            //                          System.arraycopy(TessRule.this.wallpaper.inpixels,0,TessRule.this.wallpaper.pixels,0,this.wallpaper.inpixels.length);
            //                  TessRule.this.fixVerticies(vertexX,vertexY);
            cont.applyTessellation();
            tr.firstCall=false;
            cont.wallpaper.clickCount++;
            friezeChoice.setSelectedIndex(0);
            cycleChoice.setSelectedIndex(0);
            dyhChoice.setSelectedIndex(0);
            cont.repaint();
            setCurrentBox(this);
        }
    }
}
