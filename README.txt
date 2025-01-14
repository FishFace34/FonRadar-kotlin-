# FonRadar

## **Application Description**
FonRadar is an Android application that offers financial tools. The application allows users to check exchange rates, manage check transactions, and make money growth estimates. It also provides a screen where users can easily view their requests.

---

## **Used Components and Their Roles**

1. **Activities**
- Creates different screens of the application:
- `MainActivity`: Home screen and routing center.
- `CheckTransactionsActivity`: Data entry screen for check transactions.
- `EstimateMoneyActivity`: Estimate calculation screen.
- `MyRequestsActivity`: User requests display screen.

2. **Intents**
- Provides transition between screens. For example, `MainActivity` is used to navigate to other activities.

3. **Shared Preferences**
- Saves user data. For example, the last entered amount and the calculated value are stored in the estimation calculation screen.

4. **External APIs**

- Fixer.io API is used to get exchange rates. This feature is implemented in `MainActivity`.

5. **Notifications**

- Notification is sent to the user when the estimation calculation process is completed.

---

## **Missing Components**
- **Foreground Services**: Can be used for continuous tasks.
- **Background Services**: Can be added to run API calls periodically.
- **Bound Services**: Can be useful for real-time data synchronization.
- **Broadcast Receivers**: Can be used to detect internet connection changes, for example.
- **Content Providers**: Can be added to share data with other applications.
- **Database**: Can be used to store data such as check information or historical exchange rates.

---



---

Thank you!