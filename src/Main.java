import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.*;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.*;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import org.json.*;

import java.io.*;

import java.awt.*;
import java.util.regex.*;


public class Main extends ApplicationTemplate
{
    public static class AppFrame extends ApplicationTemplate.AppFrame
    {
        //private List<PointPlacemark> list= new ArrayList<PointPlacemark>();
        private static final double ELEVATION = 1e4;
        final RenderableLayer firstLayer = new RenderableLayer();
        final RenderableLayer secondLayer = new RenderableLayer();
        final RenderableLayer thirdLayer = new RenderableLayer();
        final RenderableLayer fourthLayer = new RenderableLayer();

        Pattern pattern20 = Pattern.compile("\\/20\\d\\d");
        Pattern pattern19 = Pattern.compile("\\/19\\d\\d");
        Pattern pattern18 = Pattern.compile("\\/18\\d\\d");
        Pattern pattern17 = Pattern.compile("\\/17\\d\\d");

        public AppFrame()
        {
            super(true, true, false);

            firstLayer.setName("2000 - Present");
            secondLayer.setName("1900 - 2000");
            thirdLayer.setName("1800 - 1900");
            fourthLayer.setName("1700 - 1800");


            try{
                InputStream is = new FileInputStream("convertcsv.json");
                String jsonData =  readSource(is);
                parseLibrary(firstLayer, jsonData);
            } catch(IOException e){
                System.out.println("Error reading file...");
                e.printStackTrace();
            } catch(JSONException j){
                System.out.println("Error parsing...");
                j.printStackTrace();
            }



            // Add the baseLayer to the model.
            insertBeforeCompass(getWwd(), firstLayer);
            insertBeforeCompass(getWwd(), secondLayer);
            insertBeforeCompass(getWwd(), thirdLayer);
            insertBeforeCompass(getWwd(), fourthLayer);

        }

        void parseLibrary(RenderableLayer layer, String jsonData) throws JSONException{
            JSONArray library = new JSONArray(jsonData);

            for (int index = 0; index < library.length(); index++) {
                JSONObject meteorite = library.getJSONObject(index);
                parseMeteorite(meteorite);
            }
        }

        void parseMeteorite(JSONObject meteorite) throws JSONException{
            String name = meteorite.getString("name");
            String date = meteorite.getString("year");
            //String id = meteorite.getString("id");
            double lat = meteorite.getDouble("reclat");
            double lon = meteorite.getDouble("reclong");
            PointPlacemark pp = new PointPlacemark(Position.fromDegrees(lat, lon, ELEVATION));
            //pp.setLabelText(name + ", " + date);
            pp.setValue(AVKey.DISPLAY_NAME, (name + ", " + date));
            pp.setLineEnabled(false);
            pp.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
            pp.setEnableLabelPicking(true); // enable label picking for this placemark
            PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
            attrs.setImageAddress("./icon.png");
            attrs.setImageColor(new Color(1f, 1f, 1f, 0.6f));
            attrs.setScale(0.1);
//            attrs.setImageOffset(new Offset(19d, 8d, AVKey.PIXELS, AVKey.PIXELS));
            attrs.setLabelOffset(new Offset(0.9d, 0.6d, AVKey.FRACTION, AVKey.FRACTION));
            pp.setAttributes(attrs);

            if (pattern20.matcher(date).find()) {
                firstLayer.addRenderable(pp);
            }
            if (pattern19.matcher(date).find()) {
                secondLayer.addRenderable(pp);
            }
            if (pattern18.matcher(date).find()) {
                thirdLayer.addRenderable(pp);
            }
            if (pattern17.matcher(date).find()) {
                thirdLayer.addRenderable(pp);
            }


        }

        private String readSource(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            return sb.toString();
        }

    }



    public static void main(String[] args)
    {
        ApplicationTemplate.start("Meteorites", AppFrame.class);
    }
}
