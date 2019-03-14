package net.ravendb.demo.components.grid;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;

import com.nega.NegaPaginator;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import net.ravendb.demo.model.Patient;

public class PageableGrid<T> extends VerticalLayout {
    private static final int PAGE_SIZE=10;  
	
	@FunctionalInterface
	public interface PageableCallback<T>{
	    public Pair<Collection<T>,Integer> loadPage(int page,int pageSize);	
	}
	
	private NegaPaginator paginator = new NegaPaginator();
	private Grid<T> grid = new Grid<>();
	private final PageableCallback pageableCallback;
    private int size;
    
	public PageableGrid(PageableCallback pageableCallback) {
		this.size=PAGE_SIZE;
		paginator.setInitialPage(false);
		paginator.setSize(size);
		this.pageableCallback=pageableCallback;
		buildUI();
	}

	private void buildUI() {
		setSizeFull();
		//grid.setHeightByRows(true);
		
		paginator.setInitialPage(true);

		paginator.addPageChangeListener(e -> {
			onPageChange(e.getPage());
		});

		add(grid, paginator);
		setAlignItems(Alignment.CENTER);
	}

	private void onPageChange(int page) {
		Pair<Collection<T>,Integer> result=pageableCallback.loadPage(page,size);
		grid.setItems(result.getKey());
		paginator.setTotal(result.getValue());					
	}

	public void loadFirstPage(){
		Pair<Collection<T>,Integer> result=pageableCallback.loadPage(0,size);
		grid.setItems(result.getKey());
		paginator.setPage(0);
		paginator.setTotal(result.getValue());			
	}
	
	public Grid<T> getGrid() {
		return this.grid;
	}

	public NegaPaginator getPaginator() {
		return this.paginator;
	}

}
