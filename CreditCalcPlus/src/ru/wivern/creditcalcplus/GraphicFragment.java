package ru.wivern.creditcalcplus;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import com.androidplot.xy.FillDirection;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.androidplot.Plot;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class GraphicFragment extends Fragment implements IUpdateData {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    private XYPlot plotXY;
    
    public static GraphicFragment newInstance(int sectionNumber) {
    	//fragment=PlaceholderFragment.instantiate(getBaseContext(), MyClass1.class.getName());
    	GraphicFragment fragment = new GraphicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GraphicFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graphic, container, false);
        plotXY = (XYPlot) rootView.findViewById(R.id.plotXY);
//        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
//        textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        UpdateGraphic();
        return rootView;
    }
    
    public void UpdateGraphic()
    {
    	int i;
		plotXY.clear();
		plotXY.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		plotXY.getGraphWidget().getDomainGridLinePaint().setColor(Color.BLACK);
		plotXY.getGraphWidget().getDomainGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
		plotXY.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		plotXY.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
		plotXY.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
		plotXY.getBorderPaint().setStrokeWidth(1);
		plotXY.getBorderPaint().setAntiAlias(false);
		plotXY.getBorderPaint().setColor(Color.WHITE);

		MainActivity ma = (MainActivity) this.getActivity();
		if(ma == null)
		{
			return;
		}
		UpdateStruct upd_struct = ma.GetUpdateStruct();
		ArrayList<SparseArray<Object>> curr_data = MainActivity
				.UpdateArrayList(upd_struct);
		// initialize our XYPlot reference:

		// Create a couple arrays of y-values to plot:
		Vector<Number> list_series0 = new Vector<Number>();
		Vector<Number> list_series1 = new Vector<Number>();
		Vector<Number> list_series2 = new Vector<Number>();
		for(i = 0; i< curr_data.size(); i++)
		{
			SparseArray<Object> spArray = curr_data.get(i);
			int currPayN		= (Integer) spArray.get(MainActivity.NUM_OF_PAY_COLUMN);
			double currPayCred	= (Double) spArray.get(MainActivity.SUMM_CREDIT_COLUMN);
			double currProcent	= (Double) spArray.get(MainActivity.SUMM_PROCENT_COLUMN);
			list_series1.add(currPayCred);
			list_series2.add(currProcent);
			list_series0.add(currPayN);
		}
		//Number[] series1Numbers = { 1, 8, 5, 2, 7, 4 };
		//Number[] series2Numbers = { 4, 6, 3, 8, 2, 10 };

		// Turn the above arrays into XYSeries':
		// SimpleXYSeries takes a List so turn our array into a List
//		XYSeries series1 = new SimpleXYSeries(list_series1,
//				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use
//														// the element index as
//														// the x value
//				"Основной долг"); // Set the display title of the series
		XYSeries series1 = new SimpleXYSeries(
				list_series0,
				list_series1,
				"Основной долг");
		// same as above
//		XYSeries series2 = new SimpleXYSeries(list_series2,
//				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Проценты");
		XYSeries series2 = new SimpleXYSeries(
				list_series0,
				list_series2,
				"Проценты");
		// Create a formatter to use for drawing a series using
		// LineAndPointRenderer:
//		LineAndPointFormatter series1Format = new LineAndPointFormatter(
//				Color.rgb(0, 200, 0), // line color
//				Color.rgb(0, 100, 0), // point color
//				Color.rgb(100, 200, 0), null, FillDirection.BOTTOM); // fill color (none)
		// setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(200);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED, null, FillDirection.BOTTOM);
        formatter.setFillPaint(lineFill);
        plotXY.getGraphWidget().setPaddingRight(2);
        plotXY.addSeries(series1, formatter);
		// add a new series' to the xyplot:
		//plotXY.addSeries(series1, series1Format);

		// same as above:
		plotXY.addSeries(
				series2,
				new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.rgb(0, 0,
						100), null, null, FillDirection.BOTTOM));

		// reduce the number of range labels
		plotXY.setTicksPerRangeLabel(3);
		plotXY.setDomainValueFormat(new DecimalFormat("0"));
		// by default, AndroidPlot displays developer guides to aid in laying
		// out your plot.
		// To get rid of them call disableAllMarkup():
		plotXY.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
		plotXY.getGraphWidget().setRangeLabelWidth(50);
		plotXY.setGridPadding(0, 10, 0, 0);
		//plotXY.setDomainStepValue(1);
 
        // thin out domain/range tick labels so they dont overlap each other:
		plotXY.setTicksPerDomainLabel(5);
        plotXY.setTicksPerRangeLabel(3);
        //plotXY.set
        // freeze the range boundaries:
        //plotXY.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);
    }

	@Override
	public void UpdateInputData(UpdateStruct us) {
		UpdateGraphic();
	}
}
