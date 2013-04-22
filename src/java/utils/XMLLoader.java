/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import model.CardType;
import model.Model;
import model.Room;
import model.Suspect;
import model.Weapon;
import model.card.Card;
import model.player.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 *
 * @author guyklainer
 */
public class XMLLoader {
    
    private static final int EXIT   = -1;
    private static final int TRY    = 0;
    private static final int SUCCES = 1;

    public static boolean readXMLFile( File file, Model details, InputStream xsd ) {
        
        try {
            File settings                       = file;
            DocumentBuilderFactory dbFactory    = DocumentBuilderFactory.newInstance();
            dbFactory.setValidating( true );
            dbFactory.setNamespaceAware( true );
            
            
            DocumentBuilder dBuilder            = dbFactory.newDocumentBuilder();
            dBuilder.setErrorHandler( null );
            Document doc                        = dBuilder.parse( settings );
            
            SchemaFactory factory   = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
            
            String StringFromInputStream = convertStreamToString( xsd );
            
            Source schemaFile       = new StreamSource( new StringReader( StringFromInputStream ) );
            Schema schema           = factory.newSchema( schemaFile );
            Validator validator     = schema.newValidator();

            validator.validate( new DOMSource( doc ) );
             
            doc.getDocumentElement().normalize();

            Node xmlMurderRoom      = doc.getElementsByTagName( "murder_room" ).item( 0 ).getChildNodes().item( 0 );
            Node xmlMurderWeapon    = doc.getElementsByTagName( "murder_weapon" ).item( 0 ).getChildNodes().item( 0 );
            Node xmlMurderer        = doc.getElementsByTagName( "murderer" ).item( 0 ).getChildNodes().item( 0 );
            
            Room resultRoom         = Room.valueOf(formatStringToEnum(xmlMurderRoom.getNodeValue()));
            Weapon resultWeapon     = Weapon.valueOf(formatStringToEnum(xmlMurderWeapon.getNodeValue()));
            Suspect resultSuspect   = Suspect.valueOf(formatStringToEnum(xmlMurderer.getNodeValue()));
            
            details.addCardToEnvelope( details.getDeck().find( resultRoom ) );
            details.addCardToEnvelope( details.getDeck().find( resultWeapon ) );
            details.addCardToEnvelope( details.getDeck().find( resultSuspect ) );
            
            //details.getDeck().remove( details.getEnvelope() );
            
            NodeList xmlPlayers = doc.getElementsByTagName( "player" );

            for (int i = 0; i < xmlPlayers.getLength(); i++) {
                Node node = xmlPlayers.item( i );
                if ( !getPlayerFromXML(node, details) )
                    return false;
            }
           
            for ( Player player : details.getPlayers() ) {

                // check the cards of the players for "null" card. it means a problem in the xml file
                for (Card card : player.getCards() ) {
                    if ( card == null )
                        return false;
                }
            }

        } catch (SAXException ex ) {
            return false;
        } catch (IOException ex ) {
            return false;
        } catch (ParserConfigurationException ex ) {
            return false;
        }
        return true;
    }
    
    public static String convertStreamToString(InputStream is)
            throws IOException {
        //
        // To convert the InputStream to String we use the
        // Reader.read(char[] buffer) method. We iterate until the
        // Reader return -1 which means there's no more data to
        // read. We use the StringWriter class to produce the string.
        //
        if (is != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }
    
    private static boolean getPlayerFromXML(Node node, Model model) {
         if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = ( Element ) node;
            
            String name = element.getAttribute("name");
            if ( !validateName( model, name ) ) {
                return false;
            }
            List<Card> cards = getPlayerCardsFromXML(element, model);
            
            //model.getDeck().remove(cards);
           
            if (element.getAttribute("type").equals( "HUMAN" ) && cards != null){                       
               model.getXMLPlayers().add( new Player(name, Suspect.valueOf(formatStringToEnum(element.getAttribute("character"))), cards, true) );
           }
           else if (element.getAttribute("type").equals( "COMPUTER" ) && cards != null) {
               model.getPlayers().add( new Player(name, Suspect.valueOf(formatStringToEnum(element.getAttribute("character"))), cards, false) );
           }
        }
        
        return true;
    }
    
    private static List<Card> getPlayerCardsFromXML(Element element, Model details) {
        List<Card> cards = new ArrayList<Card>();
        NodeList nodes = element.getElementsByTagName("card");
        for (int i = 0; i < nodes.getLength(); i++){
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element cardNode = (Element) node;
                CardType type = CardType.valueOf(cardNode.getAttribute("type").toUpperCase());
                String value = cardNode.getAttribute(type.toString().toLowerCase());
                switch (type){
                    case ROOM:
                        cards.add( details.getDeck().find(Room.valueOf(formatStringToEnum(value))) );
                        break;
                    case SUSPECT:
                        cards.add( details.getDeck().find(Suspect.valueOf(formatStringToEnum(value))) );
                        break;
                    case WEAPON:
                        cards.add( details.getDeck().find(Weapon.valueOf(formatStringToEnum(value))) );
                        break;
                }              
            }
        }
        return cards;

    }
    
    private static String formatStringToEnum( String str ){
        return str.toUpperCase().trim().replace(" ", "_").replace(".", "");
    }
    
    public static boolean validateName(Model model, String name) {
        for (Player player : model.getPlayers()){
            if (name.equals(player.getName()))
                return false;
        }
        
        return true;
    }
}
