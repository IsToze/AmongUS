package fr.Toze.amongus.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ColorGetter {

	private Map<String, Color> map;
	
	public ColorGetter(List<Player> players) {
		this.map = new HashMap<>();
		
		players = new ArrayList<>(players);
		Collections.shuffle(players);
		
		Color[] colors = new Color[]{
				new Color(0, new int[]{255, 255, 255}, "https://i.imgur.com/0JIG883.png", 'r'), //BLANC
				new Color(1, new int[]{255, 128, 0}, "https://i.imgur.com/gvDwIMO.png", '6'), //ORANGE
				new Color(3, new int[]{0, 255, 255}, "https://i.imgur.com/kh5TOGf.png", 'b'), //AQUA
				new Color(4, new int[]{255, 255, 0}, "https://i.imgur.com/DdoDEad.png", 'e'), //JAUNE
				new Color(5, new int[]{0, 255, 0}, "https://i.imgur.com/5jRLiJK.png", 'a'), // LIME
				new Color(6, new int[]{255, 0, 255}, "https://i.imgur.com/3Jq22iz.png", 'd'), //ROSE
				new Color(11, new int[]{0, 0, 255}, "https://i.imgur.com/pfSfQwi.png", '1'), // BLEU FONCE
				new Color(13, new int[]{0, 102, 0}, "https://i.imgur.com/Og61KVp.png", '2'), // VERT FONCE
				new Color(14, new int[]{255, 0, 0}, "https://i.imgur.com/sXvo2qj.png", 'c'), // ROUGE
				new Color(15, new int[]{0, 0, 0}, "https://i.imgur.com/faXmGUK.png", '8')}; //NOIR
		
		for(int i = 0; i < players.size(); i++){
			map.put(players.get(i).getName(), colors[i]);
		}
		
	}

	public Map<String, Color> getMap() {
		return map;
	}
	
	public Color getColor(Player player){
		return this.map.get(player.getName());
	}
	
	public class Color{
		
		private byte data;
		private int[] rgb;
		private URL url;
		private char paragraph;
		
		public Color(int data, int[] rgb, String url, char paragraph) {
			this.data = (byte) data;
			this.rgb = rgb;
			this.paragraph = paragraph;
			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		public byte getData() {
			return data;
		}
		
		public int[] getRgb() {
			return rgb;
		}
		
		public URL getURL() {
			return url;
		}
		
		public String getName(){
			String name = CraftItemStack.asNMSCopy(new ItemStack(Material.WOOL, 0, this.data)).getName();
			name = name.substring(0, name.length()-4);
			return name.length() == 0 ? "White" : name.substring(0, name.length()-1);
		}
		
		public char getParagraph() {
			return paragraph;
		}
		
	}
	
}
