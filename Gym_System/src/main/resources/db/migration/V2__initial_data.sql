-- Inserting initial data for TRAINING_TYPES table
INSERT INTO training_types (id, training_type) VALUES (1, 'CARDIO');
INSERT INTO training_types (id, training_type) VALUES (2, 'STRENGTH');
INSERT INTO training_types (id, training_type) VALUES (3, 'FLEXIBILITY');
INSERT INTO training_types (id, training_type) VALUES (4, 'BALANCE');

-- Inserting initial data for USERS table
INSERT INTO USERS (first_name, last_name, username, password, is_active) VALUES
                                                                              ('John', 'Doe', 'John.Doe', '$2a$12$ob2GhzfnEoEJbJISrSnxdOkYy5G65mKPf3SlCE84MxF/OxmvlP5N2', true),     #pssword =  0123456789
                                                                              ('Alice', 'Smith', 'Alice.Smith', '$2a$12$fWkvKFKUvmWVmddcMUxftOyYDxjHcTGRMUGSQOOEnWBg1bwhBOkG.', true),    #password = plaintext!
                                                                              ('Bob', 'Johnson', 'Bob.Johnson', '$2a$12$BAYOfCg3UZAg3cRqRk0gQuFyJry5h1gNYfIjYCJa5qf31wKgnlG6u', true),    #password = storedwith
                                                                              ('Charlie', 'Brown', 'Charlie.Brown', '$2a$12$zDVLuA.X5TAto/.QTBcXruarUFsFIRThfEo0.pyVhJK/6UpY.4dQG', true);#password = hash&&salt

-- Inserting initial data for TRAINEES table
INSERT INTO TRAINEES (address, date_of_birth, user_id) VALUES
                                                           ('123 Main St, Cityville', '1990-05-15', 1),
                                                           ('456 Elm St, Townsville', '1992-09-20', 2);

-- Inserting initial data for TRAINERS table
INSERT INTO TRAINERS (user_id, training_type_id) VALUES
    (3, 1);

-- Inserting initial data for TRAININGS table
INSERT INTO TRAININGS (trainee_id, trainer_id, training_type_id, training_name, training_date, duration) VALUES
    (2, 1, 2, 'Strength Session 1', '2022-01-02', 90);