import {Injectable, OnDestroy} from '@angular/core';
import {ActivatedRouteSnapshot, NavigationEnd, Router} from '@angular/router';
import {BehaviorSubject, Subject, takeUntil} from 'rxjs';
import {filter} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class BreadcrumbService implements OnDestroy {
  breadcrumbs$ = new BehaviorSubject<string[]>([]);
  private destroy$ = new Subject<void>();

  constructor(private router: Router) {
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        const root = this.router.routerState.snapshot.root;
        const breadcrumbs = this.createBreadcrumbs(root);
        this.breadcrumbs$.next(breadcrumbs);
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createBreadcrumbs(route: ActivatedRouteSnapshot, url: string = '', breadcrumbs: string[] = []): string[] {
    const children: ActivatedRouteSnapshot[] = route.children;

    if (children.length === 0) {
      return breadcrumbs;
    }

    for (const child of children) {
      const routeURL: string = child.url.map(segment => segment.path).join('/');
      if (routeURL !== '') {
        url += `/${routeURL}`;
      }

      breadcrumbs = this.createBreadcrumbs(child, url, breadcrumbs);
      if (child.data && child.data['breadcrumb']) {
        breadcrumbs.push(child.data['breadcrumb']);
      }
    }
    return breadcrumbs;
  }
}