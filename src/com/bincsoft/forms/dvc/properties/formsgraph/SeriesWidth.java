package com.bincsoft.forms.dvc.properties.formsgraph;


import com.bincsoft.forms.dvc.FormsGraph;

import java.util.StringTokenizer;

import oracle.dss.util.DataDirector;
import oracle.dss.util.EdgeOutOfRangeException;
import oracle.dss.util.MetadataMap;
import oracle.dss.util.SeriesOutOfRangeException;
import oracle.dss.util.SliceOutOfRangeException;

public class SeriesWidth implements IFormsGraphProperty {
    public SeriesWidth() {
        super();
    }

    public boolean handleProperty(String sParams, FormsGraph graph) {
        if (sParams.length() > 0) {
            // separate series name from string
            StringTokenizer st = new StringTokenizer(sParams, graph.getDelimiter());
            int line_width;

            // at least two arguments must be found
            if (st.countTokens() >= 2) {

                String lineWidth = sParams.substring(sParams.indexOf(graph.getDelimiter()) + 1);
                String seriesName = sParams.substring(0, sParams.indexOf(graph.getDelimiter())).trim();

                try {
                    // get line width
                    line_width = new Integer(lineWidth.trim()).intValue();

                    if (line_width <= 0) {
                        // only positiv integers accepted
                        line_width = line_width * -1;
                    }
                } catch (NumberFormatException nfe) {
                    graph.debugMessage("SET_SERIES_WIDTH: " + sParams + " does not contain a valid line width");
                    return true;
                }

                int series_count = 0;
                try {
                    series_count = graph.getGraph().getColumnCount();
                } catch (EdgeOutOfRangeException ex) {
                    graph.debugMessage("SET_SERIES_WIDTH: " + ex);
                }

                for (int i = 0; i < series_count; i++) {
                    String shortLabel = "";
                    try {
                        shortLabel =
                                (String)graph.getGraph().getDataAccessSliceLabel(DataDirector.COLUMN_EDGE, i, MetadataMap.METADATA_LONGLABEL);
                    } catch (EdgeOutOfRangeException ex) {
                        graph.debugMessage("SET_SERIES_WIDTH: " + ex);
                    } catch (SliceOutOfRangeException ex) {
                        graph.debugMessage("SET_SERIES_WIDTH: " + ex);
                    }

                    if (seriesName.equalsIgnoreCase(shortLabel.trim())) {
                        try {
                            graph.getGraph().getSeries().setLineWidth(line_width, i);
                            graph.debugMessage("SET_SERIES_WIDTH: Series '" + seriesName + "' found");
                            break;
                        } catch (SeriesOutOfRangeException ex) {
                            graph.debugMessage("SET_SERIES_WIDTH: " + ex);
                            return true;
                        }
                    }
                }
            } else {
                graph.debugMessage("SET_SERIES_WIDTH: not enough arguments specified in " + sParams);
            }

        } else {
            graph.debugMessage("SET_SERIES_WIDTH: no argument specified");
        }
        return true;
    }
}
