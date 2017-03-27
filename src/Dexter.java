
import javax.swing.text.*;
import javax.swing.border.CompoundBorder;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.net.*;

class Dexter
{
	private static final String version = "1.0";
	private static final String window_title = "Dexter v" + version;
	public static final String IMG_CACHE = "cache\\img";
	
	public static final String DEX_FILE = "DexterConfig.xml";
	public static final String USER_FILE = "userdata.txt";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////
	private Dex dex = null;
	private String searchText = "";
	private int [] filterState = null;
	private int filterCapturedState = 0;
	private boolean showFilters = false;
	private int selectedMonId = -1;
	private int current_result_count = 0;
	private int current_captured_count = 0;
	
	private JFrame frame = null;
	private JPanel leftPanel = null;
	private JPanel rightPanel = null;
	private JPanel searchPanel = null;
	private JTextField txtSearch = null;
	private JLabel lblResultCount = null;
	private JPanel filterPanel = null;
	private JPanel resultHolder = null;
	private JPanel statsPanel = null;
	private JLabelX lblCompletion1 = null;
	private JLabelX lblCompletion2 = null;
	private JPanel fullViewPanel = null;
	private JButton btnSave = null;
 
    public static void main(String[] args)
	{
		Dexter d = new Dexter();
    }
	
	Dexter()
	{
		dex = new Dex(DEX_FILE, USER_FILE);
		createAndShowGUI();
		DrawDex(Search());
	}

	private void createAndShowGUI()
	{
        //Create and set up the window.
		frame = new JFrame("Dexter v1.0");
		frame.addWindowListener( new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				JFrame frame = (JFrame)e.getSource();

				if(dex.hasChanged())
				{
					int result = JOptionPane.showConfirmDialog(
						frame,
						"Do you want to save changes made to your dex?",
						"Exit Application",
						JOptionPane.YES_NO_OPTION);

					if (result == JOptionPane.YES_OPTION)
					{
						dex.SaveUserData(USER_FILE);
					}
				}
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}
		});
		frame.setSize(800,600);
		
		// Center in screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);
		frame.setLayout(new GridLayout(0,2));
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Search Panel
		txtSearch = new JTextField();
		txtSearch.getDocument().addDocumentListener(new DocumentListener()
		{
			// implement the methods
			public void insertUpdate(DocumentEvent e) {runUpdate();}
			public void removeUpdate(DocumentEvent e) {runUpdate();}
			public void changedUpdate(DocumentEvent e) {/*not fired*/}
			
			String scheduled_search_text = "";
			private void runUpdate()
			{
				scheduled_search_text = txtSearch.getText().trim();
				final String input_text = scheduled_search_text;
				
				Thread searchThread = new Thread()
				{
					public void run()
					{
						try{Thread.sleep(500);}catch(Exception err){}
						if(input_text.equals(scheduled_search_text) && !input_text.equals(searchText))
						{
							System.out.println("Searching for \"" + input_text + "\"...");
							searchText = input_text;
							
							DrawDex(Search());							
						}
					}
				};
				searchThread.start();
			}
		});
		
		lblResultCount = new JLabel();
		lblResultCount.setText("Initialising...");
		
		final JButton btnFilters = new JButton("Show Filters");
		btnFilters.setMargin(new Insets(0, 0, 0, 0));
		
		final GridBagConstraints filterPanelConstraints = new GridBagConstraints();
		filterPanelConstraints.gridx = 0;
		filterPanelConstraints.gridy = 1;
		filterPanelConstraints.weightx = 100;
		filterPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
		btnFilters.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				if(showFilters)
				{
					showFilters = false;
					leftPanel.remove(filterPanel);
					leftPanel.revalidate();
					leftPanel.repaint();
					btnFilters.setText("Show Filters");
				}
				else
				{
					showFilters = true;
					leftPanel.add(filterPanel, filterPanelConstraints);
					leftPanel.revalidate();
					leftPanel.repaint();
					btnFilters.setText("Hide Filters");
				}
			}
		});
		
		
		
		searchPanel = new JPanel();
		searchPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Search"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		searchPanel.setLayout(new BorderLayout());
		searchPanel.add(txtSearch, BorderLayout.NORTH);
		searchPanel.add(lblResultCount, BorderLayout.CENTER);
		searchPanel.add(btnFilters, BorderLayout.EAST);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Filter Panel
		filterPanel = new JPanel();
		filterPanel.setLayout(new GridLayout(0,1));
		filterPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Filters"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		FilterButton_SHO btnCapture = new FilterButton_SHO("Captured")
		{
			public void onStateChange(int state)
			{
				if(filterCapturedState!=state)
				{			
					filterCapturedState = state;
					DrawDex(Search());
				}
			}
		};
		filterCapturedState = btnCapture.getState();
		filterPanel.add(btnCapture);
		
		DexMonFilter [] filters = dex.getFilters();
		filterState = new int[filters.length];
		for(int i=0;i<filters.length;i++)
		{
			final int index = i;
			FilterButton_SHO btn_filter = new FilterButton_SHO(filters[i].name)
			{
				public void onStateChange(int state)
				{
					if(filterState[index]!=state)
					{					
						filterState[index] = state;
						DrawDex(Search());
					}
				}
			};
			filterState[i] = btn_filter.getState();
			filterPanel.add(btn_filter);
		}
		
		// holder for output
		resultHolder = new JPanel();
		resultHolder.setLayout(new BorderLayout());
		
		JScrollPane resultScroller = new JScrollPane(resultHolder,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		resultScroller.getVerticalScrollBar().setUnitIncrement(16);
		resultScroller.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Results"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		
		leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());

		GridBagConstraints searchPanelConstraints = new GridBagConstraints();
		searchPanelConstraints.gridx = 0;
		searchPanelConstraints.gridy = 0;
		searchPanelConstraints.weightx = 100;
		searchPanelConstraints.fill =  GridBagConstraints.HORIZONTAL;
		leftPanel.add(searchPanel, searchPanelConstraints);
		
		GridBagConstraints resultScrollerConstraints = new GridBagConstraints();
		resultScrollerConstraints.gridx=0;
		resultScrollerConstraints.gridy=2;
		resultScrollerConstraints.weightx = 100;
		resultScrollerConstraints.weighty = 100;
		resultScrollerConstraints.fill = GridBagConstraints.BOTH;
		leftPanel.add(resultScroller, resultScrollerConstraints);		
		
		frame.add(leftPanel);
		
		
		
		
		
		// create right side view and buttons
		
		final JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		statsPanel = new JPanel();
		statsPanel.setLayout(new GridBagLayout());
		statsPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Completion Stats"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		btnSave = new JButton("Save Data");
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				
				dex.SaveUserData(USER_FILE);
				btnSave.setEnabled(false);
			}
		});
		GridBagConstraints btnSaveConstraints = new GridBagConstraints();
		btnSaveConstraints.gridx=2;
		btnSaveConstraints.gridy=0;
		btnSaveConstraints.gridheight=2;
		statsPanel.add(btnSave, btnSaveConstraints);
		
		
		GridBagConstraints lblCompletion1ConstraintsLabel =  new GridBagConstraints();
		lblCompletion1ConstraintsLabel.anchor = GridBagConstraints.NORTHWEST;
		lblCompletion1ConstraintsLabel.gridx = 0;
		lblCompletion1ConstraintsLabel.gridy = 0;
		statsPanel.add(new JLabel("Completion: "), lblCompletion1ConstraintsLabel);
		
		
		lblCompletion1 = new JLabelX("???");
		lblCompletion1.setBold(false);
		GridBagConstraints lblCompletion1Constraints =  new GridBagConstraints();
		lblCompletion1Constraints.anchor = GridBagConstraints.NORTHWEST;
		lblCompletion1Constraints.gridx = 1;
		lblCompletion1Constraints.gridy = 0;
		lblCompletion1Constraints.weightx = 100;
		statsPanel.add(lblCompletion1, lblCompletion1Constraints);	

		GridBagConstraints lblCompletion2ConstraintsLabel =  new GridBagConstraints();
		lblCompletion2ConstraintsLabel.anchor = GridBagConstraints.NORTHWEST;
		lblCompletion2ConstraintsLabel.gridx = 0;
		lblCompletion2ConstraintsLabel.gridy = 1;
		statsPanel.add(new JLabel("Results: "), lblCompletion2ConstraintsLabel);		
		
		lblCompletion2 = new JLabelX("???");
		lblCompletion2.setBold(false);
		GridBagConstraints lblCompletion2Constraints =  new GridBagConstraints();
		lblCompletion2Constraints.anchor = GridBagConstraints.NORTHWEST;
		lblCompletion2Constraints.gridx = 1;
		lblCompletion2Constraints.gridy = 1;
		lblCompletion2Constraints.weightx = 100;
		statsPanel.add(lblCompletion2, lblCompletion2Constraints);
		
		
		fullViewPanel = new JPanel();
		fullViewPanel.setLayout(new BorderLayout());
		fullViewPanel.setBorder(new CompoundBorder(BorderFactory.createTitledBorder("Detailed View"), BorderFactory.createEmptyBorder(5,5,5,5)));
		
		rightPanel.add(statsPanel, BorderLayout.NORTH);
		rightPanel.add(fullViewPanel, BorderLayout.CENTER);
		
		frame.add(rightPanel);
		
        //frame.pack(); // <-- auto resizes entire frame
        frame.setVisible(true);
    }
	
	private DexResult Search()
	{
		DexResult result = dex.getAll();
		DexMonFilter [] filters = dex.getFilters();
		
		String txt = txtSearch.getText().trim();
		if(txt.length()>0)
		{
			result = result.filterName(txt);
		}
		
		if(filterCapturedState!=0)
		{
			result = result.filterCaptured(filterCapturedState);
		}
		
		
		for(int i=0;i<filters.length;i++)
		{
			if(filters[i].type.equals("sho") && filterState[i]!=0)
			{
				result = result.applySHOFilter(i, filterState[i]);
			}
		}
		return result;
	}
	
	private void DrawDex(DexResult dex_result)
	{
		final JPanel monList = new JPanel();
		monList.setLayout(new GridLayout(0,1));
		int cap_count = 0;
	
		int cnt = dex_result==null?0:dex_result.size();
		for(int i=0;i<cnt;i++)
		{
			DexMonListView v = new DexMonListView(dex_result.get(i))
			{
				protected void onClick(DexMon mon)
				{
					this.setSelected();
					ViewMon(mon);
				}
				protected void onToggleClick(DexMon mon)
				{					
					if(mon.captured)
					{
						dex.setCaptured(mon.id);
						current_captured_count++;
						btnSave.setEnabled(true);
					}
					else
					{
						dex.setNotCaptured(mon.id);
						current_captured_count--;
						btnSave.setEnabled(true);
					}
						
					updateStats();
				}
			};
			
			if(selectedMonId==-1)
			{
				v.setSelected();
				ViewMon(dex_result.get(i));
			}
			else if(dex_result.get(i).id==selectedMonId)
			{
				v.setSelected();
			}
			
			// increase capture count
			if(dex_result.get(i).captured) cap_count++;
			
			monList.add(v);
		}
		
		current_result_count = cnt;
		current_captured_count = cap_count;
		updateStats();
		
		lblResultCount.setText(cnt + " " + (cnt==1?"result":"results"));
		resultHolder.removeAll();
		resultHolder.add(monList, BorderLayout.NORTH);
		resultHolder.revalidate();
		frame.repaint();
	}
	
	private void ViewMon(final DexMon mon)
	{
		if(selectedMonId==mon.id) return;
		selectedMonId = mon.id;
		
		DexMonFullView monDetail = new DexMonFullView(mon, dex.getFilters());		
		
		fullViewPanel.removeAll();
		fullViewPanel.add(monDetail);
		fullViewPanel.revalidate();
		frame.repaint();
	}
	
	private void updateStats()
	{
		String s_c = dex.user_count_these + " / " + dex.dex_count;
		String r_c = current_captured_count + " / " + current_result_count;
		
		if(dex.user_count_extra>0) s_c += " (+" + dex.user_count_extra + ")";
		
		int decimals = 2;
		s_c += "   " + perc(dex.user_count_these, dex.dex_count, decimals) + "%";
		r_c += "   " + perc(current_captured_count, current_result_count, decimals) + "%";
		
		lblCompletion1.setText(s_c);
		lblCompletion2.setText(r_c);
	}
	
	private String perc(int x, int y, int decimals)
	{
		double div = 100.0;
		double mult = 1.0;
		for(int i=0;i<decimals;i++)
		{
			div *= 10;
			mult *=10;
		}
		
		int perc = (int)(x*div/y);
		
		return String.valueOf((perc*1.0/mult));
	}
	public static void openWebpage(String url)
	{
		try
		{
			URI uri = new URI(url);
			Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
			if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			{
				desktop.browse(uri);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}	
}