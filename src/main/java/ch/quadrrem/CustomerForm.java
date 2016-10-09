package ch.quadrrem;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by remo on 09.10.16.
 */
public class CustomerForm extends FormLayout {

  private final TextField firstName = new TextField("First name");
  private final TextField lastName = new TextField("Last name");
  private final TextField email = new TextField("Email");
  private final NativeSelect status = new NativeSelect("Status");
  @PropertyId("birthDate")
  private final PopupDateField birthday = new PopupDateField("Birthday");
  private final Button save = new Button("Save");
  private final Button delete = new Button("Delete");

  private final CustomerService customerService = CustomerService.getInstance();
  private final MyUI myUI;
  private Customer customer;

  public CustomerForm(final MyUI myUI) {
    this.myUI = myUI;

    initUI();
  }

  private void initUI() {
    setSizeUndefined();

    status.addItems(CustomerStatus.values());

    save.setStyleName(ValoTheme.BUTTON_PRIMARY);
    save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
    save.addClickListener(e -> save());

    delete.addClickListener(e -> delete());

    final HorizontalLayout buttons = new HorizontalLayout(save, delete);
    buttons.setSpacing(true);
    addComponents(firstName,
                  lastName,
                  email,
                  birthday,
                  status,
                  buttons,
                  buttons);
  }

  private void delete() {
    customerService.delete(customer);
    myUI.updateList();
    setVisible(false);
  }

  private void save() {
    customerService.save(customer);
    myUI.updateList();
    setVisible(false);
  }

  public void setCustomer(final Customer customer) {
    this.customer = customer;
    BeanFieldGroup.bindFieldsUnbuffered(customer, this);
    delete.setVisible(customer.isPersisted());
    setVisible(true);
    firstName.selectAll();
  }
}
