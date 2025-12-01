import { test, expect } from '@playwright/test';

test.describe('Happy Path and Search Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Mock for initial load and when clearing search
    await page.route('**/api/releases', async (route) => {
      const json = [
        { id: 1, artist: 'Radiohead', title: 'OK Computer', year: 1997, coverUrl: '' },
        { id: 2, artist: 'The Beatles', title: 'Abbey Road', year: 1969, coverUrl: '' },
        { id: 3, artist: 'Pink Floyd', title: 'The Dark Side of the Moon', year: 1973, coverUrl: '' },
      ];
      await route.fulfill({ json });
    });

    // Mock for the genre list
    await page.route('**/api/genres', async (route) => {
      const json = ['Rock', 'Pop', 'Electronic', 'Jazz'];
      await route.fulfill({ json });
    });

    // Mock for the search functionality using a more general glob pattern
    await page.route('**/api/home?search=*', async (route) => {
      // Check the actual search query if needed, for now, return a standard search result
      const url = route.request().url();
      if (url.includes('Radiohead')) {
        const json = [{ id: 1, artist: 'Radiohead', title: 'OK Computer', year: 1997, coverUrl: '' }];
        await route.fulfill({ json });
      } else {
        // Return empty for other searches if necessary, or a default list
        await route.fulfill({ json: [] });
      }
    });
  });

  test('startsidan visar album-kort och genrer', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('app-release-card').first()).toBeVisible();
    await expect(page.locator('app-genre-list')).toBeVisible();
  });

  test('sökflöde: sökning minskar lista, rensa visar alla igen', async ({ page }) => {
    await page.goto('/');
    const searchInput = page.getByPlaceholder('Search by title or artist...');

    await expect(page.locator('app-release-card').first()).toBeVisible();

    const initialCount = await page.locator('app-release-card').count();
    expect(initialCount).toBe(3);

    await searchInput.fill('Radiohead');
    await expect(page).toHaveURL(/.*\?q=Radiohead/);

    // The general search mock will catch this and return the correct data
    await expect(page.locator('app-release-card')).toHaveCount(1);

    await searchInput.clear();
    await expect(page).not.toHaveURL(/.*\?q=.+/);

    // The initial load mock ('**/api/releases') will be used here
    await expect(page.locator('app-release-card')).toHaveCount(initialCount);
  });

  test('sökflöde: query i URL funkar på reload', async ({ page }) => {
    // The general search mock will handle this direct navigation
    await page.goto('/?q=Radiohead');

    await expect(page.locator('app-release-card').first()).toBeVisible();

    const count = await page.locator('app-release-card').count();
    expect(count).toBe(1);

    await expect(page.getByPlaceholder('Search by title or artist...')).toHaveValue('Radiohead');
  });
});
