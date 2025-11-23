import { test, expect } from '@playwright/test';

test.describe('Happy Path and Search Flow', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('startsidan visar album-kort och genrer', async ({ page }) => {
    // Vänta på att korten ska laddas och verifiera att det finns minst ett
    await expect(page.locator('app-release-card').first()).toBeVisible();

    // Verifiera att genre-listan är synlig
    await expect(page.locator('app-genre-list')).toBeVisible();
  });

  test('sökflöde: sökning minskar lista, rensa visar alla igen', async ({ page }) => {
    const searchInput = page.getByPlaceholder('Search by title or artist...');

    // Vänta på att korten ska laddas innan vi fortsätter
    await expect(page.locator('app-release-card').first()).toBeVisible();

    // Hämta initiala antalet kort
    const initialCount = await page.locator('app-release-card').count();
    expect(initialCount).toBeGreaterThan(1);

    // Sök på en specifik artist/album
    await searchInput.fill('Radiohead');

    await expect(page).toHaveURL(/.*\?q=Radiohead/);

    // Vänta på resultatet: att listan med kort har minskat.
    // Förväntas att sökningen på "Radiohead" ger färre resultat än den fullständiga listan
    await expect(page.locator('app-release-card')).not.toHaveCount(initialCount);

    // Rensa sökningen
    await searchInput.clear();

    // Vänta på att URL:en återställs (att q-parametern försvinner)
    await expect(page).not.toHaveURL(/.*\?q=.+/);

    // Nu bör listan vara återställd till sitt ursprungliga antal
    await expect(page.locator('app-release-card')).toHaveCount(initialCount);
  });

  test('sökflöde: query i URL funkar på reload', async ({ page }) => {
    // Navigera direkt till en URL med en sökfråga
    await page.goto('/?q=Radiohead');

    // Vänta på att sökresultatet ska laddas
    await expect(page.locator('app-release-card').first()).toBeVisible();

    // Verifiera att sökningen är applicerad direkt
    const count = await page.locator('app-release-card').count();
    expect(count).toBeGreaterThan(0);

    // Verifiera att input-fältet har rätt värde
    await expect(page.getByPlaceholder('Search by title or artist...')).toHaveValue('Radiohead');
  });
});
