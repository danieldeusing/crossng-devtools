import {Component} from '@angular/core';
import {routes} from 'src/app/app-routing.module';
import {Route} from '@angular/router';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent {
  public categorizedRoutes: { [key: string]: Route[] } = {};

  constructor() {
    routes.forEach(route => {
      if (route.data?.['navCategory']) {
        const category = route.data['navCategory'];
        this.categorizedRoutes[category] = this.categorizedRoutes[category] || [];

        if (route.children) {
          route.children.forEach(childRoute => {
            if (childRoute.data) {
              this.categorizedRoutes[category].push(childRoute);
            }
          });
        }
      }
    });
  }

}
