package news2;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JList;
import javax.swing.border.MatteBorder;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

//import HomeForm.MyListCellRenderer;

//import test.Test3.MyListCellRenderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.net.URI;
import java.awt.event.ActionEvent;
import javax.swing.ListSelectionModel;

public class HomeForm extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	
	//listmodel used to populate list
	DefaultListModel<Article> dlm;

	// Launch
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HomeForm frame = new HomeForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// Setup mongoDB connection
		String connectionString = "mongodb+srv://Cluster70987:<password>@cluster70987.azzefpa.mongodb.net/?retryWrites=true&w=majority";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        // Create a new client and connect to the server
        try (MongoClient mongoClient = MongoClients.create(settings)) {
            try {
                // Send a ping to confirm a successful connection
                MongoDatabase database = mongoClient.getDatabase("admin");
                database.runCommand(new Document("ping", 1));
                System.out.println("Pinged your deployment. You successfully connected to MongoDB!");
            } catch (MongoException e) {
                e.printStackTrace();
            }
        }
	}
	
	// Highlight selected article for clarity
	private class MyListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            return this;
        }

    }
	
	// iterate mongoDB document and construct documents as objects for data
	private static DefaultListModel<Article> DbQuery() {
		
		DefaultListModel<Article> arr = new DefaultListModel<Article>();
		
		String uri = "mongodb+srv://Cluster70987:Xm1MTEJIXFxD@cluster70987.azzefpa.mongodb.net/?retryWrites=true&w=majority";
		try (MongoClient mongoClient = MongoClients.create(uri)) {
	        MongoDatabase database = mongoClient.getDatabase("DB1");
	        MongoCollection<Document> collection = database.getCollection("DBArticles");
	        FindIterable<Document> doc = collection.find();
	        
	        if (doc != null) {
	        	Iterator it = doc.iterator();
		        // Get list of database articles
		        while (it.hasNext()) {
		           //System.out.println(it.next());
		           Object element =  it.next();
		           System.out.println(((Document) element).get("description").toString());
		           String title = ((Document) element).get("title").toString();
		           String description = ((Document) element).get("description").toString();
		           String content = ((Document) element).get("content").toString();
		           String publishedAt = ((Document) element).get("publishedAt").toString();
		           String url = ((Document) element).get("url").toString();
		           
		           Article a = new Article(title, url, description,publishedAt, content);
		           arr.addElement(a);
		        }

	        } else {
	            System.out.println("No matching documents found.");
	        }
	    }
		System.out.println("return value: "+arr.toString());
		return arr;
	}

	
	// open link of desired article in default browser
	public static boolean openWebpage(URI uri) {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
        try {
            desktop.browse(uri);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return false;
}

	// Construct string values of listmodel based on data fetched from DB
	public String[] populate(DefaultListModel<Article> list) {
		
		String[] value = new String[] {"", "", "", "", "", "", "", "", "", ""};
		int leng = list.size();
		
		for(int i = 0; i<leng; i++ ) {
			value[i] =
					list.elementAt(i).getTitle();
		}
		return value;
	}
	
	// DB operation for deletion of single document. Ran in thread due to time expense.
	public void deleteOne(int index, DefaultListModel<Article> list) {
		String uri = "mongodb+srv://Cluster70987:Xm1MTEJIXFxD@cluster70987.azzefpa.mongodb.net/?retryWrites=true&w=majority";
		try (MongoClient mongoClient = MongoClients.create(uri)) {
	        MongoDatabase database = mongoClient.getDatabase("DB1");
	        MongoCollection<Document> collection = database.getCollection("DBArticles");
	        
	        String title = list.getElementAt(index).getTitle();
			collection.deleteOne(new Document("title", title));
		} catch (MongoException err) {
            System.err.println("Unable to delete due to an error: " + err);
        }
	}
		
	// Create the frame
	public HomeForm() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 559, 451);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(15, 15, 25, 15));

		setContentPane(contentPane);
		
		final JList jlist = new JList();
		jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		dlm = DbQuery();
		
		JButton deleteBtn = new JButton("Delete item");
		deleteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final int del = jlist.getSelectedIndex();
				
				dlm.removeElementAt(del);
				
				jlist.setModel(new AbstractListModel() {
					String[] values = populate(dlm);
					
					public int getSize() {
						return values.length;
					}
					public Object getElementAt(int index) {
						return values[index];
					}
				});
				
				Thread deletion = new Thread(new Runnable() {
					@Override
					public void run() {
					    System.out.println("deletion in thread...");
					    deleteOne(del, dlm);
					}
				});
				deletion.start();
				 
		}});
		deleteBtn.setVerticalAlignment(SwingConstants.BOTTOM);
		contentPane.add(deleteBtn);
		
		
		
		JButton readBtn = new JButton("Read online");
		readBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final int selected = jlist.getSelectedIndex();
				String uri = dlm.get(selected).getUrl();
				URI wp = URI.create(uri);
				openWebpage(wp);	
			}
		});
		readBtn.setVerticalAlignment(SwingConstants.BOTTOM);
		contentPane.add(readBtn);
		jlist.setModel(new AbstractListModel() {
			String[] values = populate(dlm);
			
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		jlist.setCellRenderer(new MyListCellRenderer());
		jlist.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		contentPane.add(jlist);
	}

}