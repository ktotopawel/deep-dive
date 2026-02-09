Project Status:
We have successfully refactored the application into a Spring Modulith architecture. We successfully separated the „School” (Training Module) from the „Work” (Ingestion/Live App).
 * Ingestion Module: Contains the RSS logic, Article entities, and FeedRefinery. This is the production code.
 * Training Module: Contains the Machine Learning experiments. This is where we are currently working.
 * Database: PostgreSQL is running in Docker. The ingestion tables exist, but the training tables are not yet created.
The Current Logic (The „Deep Fetch”):
We are building the data ingestion pipeline to create a „Gold Standard” dataset for training a Naive Bayes classifier.
 * Source: Dev.to API.
 * Strategy: Since the API hides the full text in lists, we use a „List \rightarrow Detail” loop:
   * Fetch a page of articles by tag (e.g., #tutorial).
   * Iterate through IDs.
   * Fetch the full body_markdown for each ID (with a 150ms delay to respect rate limits).
   * Map the tags to our internal Enums (Label.TUTORIAL, LabelSource.DEV_TO_API).
   * Save to the training_data table.
📋 Next 3 Specific Tasks
1. Build the „Bucket” (Persistence Layer)
We defined the Enums conceptually, but the files do not exist yet.
 * Action: Create the Label and LabelSource enums.
 * Action: Create the TrainingDataEntity (using url as PK and TEXT for body) and TrainingDataRepository inside com.ktotopawel.deepdive.training.adapter.persistence.
2. Build the „Hose” (The Loader Service)
We have the raw logic, but it needs to be assembled into the final class with the new Enums.
 * Action: Create DevToDatasetLoader in training.adapter.devto.
 * Logic: Implement the while(collected < quota) loop that handles pagination and the „Deep Fetch” API calls.
 * Mapping: Ensure string tags from the API („changelog”) map correctly to our Enums (Label.RELEASE_NOTE).
3. The „Ignition” (Execution & Verification)
We need a way to run this loader without starting the whole web server or polluting the production flow.
 * Action: Create a TrainingDataRunner (implementing CommandLineRunner) annotated with @Profile(„training”).
 * Verification: Run the app with -Dspring.profiles.active=training, then query Postgres to confirm we have ~200-300 full-text articles labeled by category.
 * 