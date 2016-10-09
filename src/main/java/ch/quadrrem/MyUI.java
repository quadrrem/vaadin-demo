package ch.quadrrem;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import javax.servlet.annotation.WebServlet;
import java.util.List;

/**
 * This UI is the application entry point. A UI may either represent a browser window
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

  private final CustomerService service = CustomerService.getInstance();
  private final Grid grid = new Grid();
  private final TextField filterText = new TextField();
  private final CustomerForm form = new CustomerForm(this);

  @Override
  protected void init(final VaadinRequest vaadinRequest) {
    final VerticalLayout layout = new VerticalLayout();

    filterText.setInputPrompt("filter by name...");
    filterText.addTextChangeListener(e -> updateList(e.getText()));

    final Button clearFilter = new Button(FontAwesome.TIMES);
    clearFilter.setDescription("Clear the current Filter");
    clearFilter.addClickListener(e -> {
      filterText.clear();
      updateList();
    });

    final CssLayout cssLayout = new CssLayout(filterText, clearFilter);
    cssLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

    final Button addCustomer = new Button("Add new customer");
    addCustomer.addClickListener(e -> {
      grid.select(null);
      form.setCustomer(new Customer());
    });

    final HorizontalLayout toolbar = new HorizontalLayout(cssLayout, addCustomer);
    toolbar.setSpacing(true);

    grid.setColumns("firstName", "lastName", "email");
    grid.addSelectionListener(e -> {
      if(e.getSelected().isEmpty()) {
        form.setVisible(false);
      } else {
        final Customer customer = (Customer) e.getSelected().iterator().next();
        form.setCustomer(customer);
      }
    });
    updateList();

    form.setVisible(false);

    final HorizontalLayout main = new HorizontalLayout(grid, form);
    main.setSpacing(true);
    main.setSizeFull();
    grid.setSizeFull();
    main.setExpandRatio(grid, 1);

    layout.setMargin(true);
    layout.addComponents(toolbar, main);

    setContent(layout);
  }

  void updateList() {
    updateList(filterText.getValue());
  }

  private void updateList(final String filterText) {
    final List<Customer> customers = service.findAll(filterText);
    grid.setContainerDataSource(new BeanItemContainer<>(Customer.class, customers));
  }

  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
  public static class MyUIServlet extends VaadinServlet {
  }
}
