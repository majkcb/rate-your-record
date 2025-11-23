import { test, expect } from '@playwright/test';

test.describe('API Error Handling', () => {

  test('ska visa ett felmeddelande om API:et för releases misslyckas', async ({ page }) => {
    // Mocka API-anropet för att returnera ett 500-fel
    await page.route('**/api/releases', route => {
      route.fulfill({ status: 500, body: 'Internal Server Error' });
    });

    await page.goto('/');

    // Verifiera att felmeddelandet visas
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.getByText('Could not load releases. Please try again.')).toBeVisible();

    // Verifiera att inga album-kort visas
    await expect(page.locator('app-release-card')).toHaveCount(0);
  });

  test('ska ladda om albumen när användaren klickar på "Försök igen"', async ({ page }) => {
    let requestCount = 0;

    // Mocka API-anropet. Första gången misslyckas det, andra gången lyckas det.
    await page.route('**/api/releases', async route => {
      requestCount++;
      if (requestCount === 1) {
        return route.fulfill({ status: 500 });
      }
      // Fortsätt med det riktiga anropet andra gången
      return route.continue();
    });

    await page.goto('/');

    // Felmeddelandet ska visas initialt
    await expect(page.locator('.error-message')).toBeVisible();

    // Klicka på "Försök igen"
    await page.getByRole('button', { name: 'Try again' }).click();

    // Nu ska felmeddelandet vara borta
    await expect(page.locator('.error-message')).not.toBeVisible();

    // Och albumen ska ha laddats in
    await expect(page.locator('app-release-card').first()).toBeVisible();
  });
});
