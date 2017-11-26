package com.ciaoshen.howtomcatworks.ex10.realm;
// modification of org.apache.catalina.realm.UserDatabaseRealm

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.catalina.Group;
import org.apache.catalina.Role;
import org.apache.catalina.User;
import org.apache.catalina.UserDatabase;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.realm.RealmBase;
import org.apache.catalina.users.MemoryUserDatabase;

public class SimpleUserDatabaseRealm extends RealmBase {

  protected UserDatabase database = null;
  protected static final String name = "SimpleUserDatabaseRealm";

  protected String resourceName = "UserDatabase";

  public Principal authenticate(String username, String credentials) {
    // Does a user with this username exist?
    User user = database.findUser(username);
    if (user == null) {
      return (null);
    }

    System.out.println("User Name: [" + username + "] exist!");

    /** 检查用户名和密码 */
    // Do the credentials specified by the user match?
    // FIXME - Update all realms to support encoded passwords
    boolean validated = false;
    if (hasMessageDigest()) {
      // Hex hashes should be compared case-insensitive
      validated = (digest(credentials).equalsIgnoreCase(user.getPassword()));
    } else {
      validated = (digest(credentials).equals(user.getPassword()));
    }
    if (!validated) {
      return null;
    }

    System.out.println("Username and Password: [" + username + "," + credentials + "] pass!");

    /** 收集用户角色集合(ArrayList<String>) */
    ArrayList combined = new ArrayList();
    Iterator roles = user.getRoles(); // 权限集在User对象中，是一个ArrayList
    while (roles.hasNext()) {
      Role role = (Role) roles.next();
      String rolename = role.getRolename();
      if (!combined.contains(rolename)) {
        combined.add(rolename);
      }
    }
    /** 收集用户分组中的角色(ArrayList<String>) */
    Iterator groups = user.getGroups(); // 分组集在User对象中也是一个ArrayList
    while (groups.hasNext()) {
      Group group = (Group) groups.next();
      roles = group.getRoles();
      while (roles.hasNext()) {
        Role role = (Role) roles.next();
        String rolename = role.getRolename();
        if (!combined.contains(rolename)) {
          combined.add(rolename);
        }
      }
    }

    System.out.println("User: [" + username + "] has roles: " + combined);

    /** 将用户的用户名，密码，角色信息封装成一个Pricipal返回 */
    return (new GenericPrincipal(this, user.getUsername(),
      user.getPassword(), combined));
  }

  // ------------------------------------------------------ Lifecycle Methods


    /**
     * Prepare for active use of the public methods of this Component.
     *
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents it from being started
     */
  protected Principal getPrincipal(String username) {
    return (null);
  }

  protected String getPassword(String username) {
    return null;
  }

  protected String getName() {
    return this.name;
  }

  public void createDatabase(String path) {
    System.out.println("Call createDatabase() method!");
    database = new MemoryUserDatabase(name);
    System.out.println("createDatabase() method create MemoryUserDatabase!");
    ((MemoryUserDatabase) database).setPathname(path);
    System.out.println("createDatabase() method set pathname to " + path + "!");
    try {
      System.out.println("Reading from database file: tomcat-users.xml");
      database.open();
      System.out.println("User database created!");
    } catch (Exception e)  {
        System.out.println(e + "occured when reading datas from tomcat-users.xml");
    }
    Iterator userIte = database.getUsers();
    while (userIte.hasNext()) {
        User u = (User)userIte.next();
        System.out.println("User: " + u.getName());
        System.out.println("\tPassword: " + u.getPassword());
        System.out.print("\tRoles: [");
        Iterator roleIte = u.getRoles();
        while (roleIte.hasNext()) {
            System.out.print(((Role)roleIte.next()).getRolename());
            if (roleIte.hasNext()) { System.out.print(","); }
        }
        System.out.print("]\n");
    }
  }
}
