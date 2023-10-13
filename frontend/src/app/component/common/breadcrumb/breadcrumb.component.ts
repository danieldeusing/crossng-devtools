import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {filter} from 'rxjs/operators';
import {Subject, takeUntil} from 'rxjs';

interface Breadcrumb {
  label: string;
  url: string;
}

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  breadcrumbs: Breadcrumb[] = [];

  private destroy$ = new Subject<void>();

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      const root: ActivatedRoute = this.activatedRoute.root;
      this.breadcrumbs = this.createBreadcrumbs(root);
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createBreadcrumbs(route: ActivatedRoute, url: string = '', breadcrumbs: Breadcrumb[] = []): Breadcrumb[] {
    if (!route.parent) {
      breadcrumbs.push({label: 'Home', url: '/'});
    }

    if (route.routeConfig && route.routeConfig.data && route.routeConfig.data['breadcrumb']) {
      const newBreadcrumb: Breadcrumb = {
        label: route.routeConfig.data['breadcrumb'],
        url: url + `/${route.routeConfig.path}`
      };
      breadcrumbs.push(newBreadcrumb);
    }

    if (route.firstChild) {
      return this.createBreadcrumbs(route.firstChild, url + (route.routeConfig ? `/${route.routeConfig.path}` : ''),
        breadcrumbs);
    }
    return breadcrumbs;
  }
}
