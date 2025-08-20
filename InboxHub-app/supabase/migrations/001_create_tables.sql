-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create api_configs table
CREATE TABLE api_configs (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    name VARCHAR(255) NOT NULL,
    url VARCHAR(1024) NOT NULL,
    method VARCHAR(10) DEFAULT 'POST' NOT NULL,
    headers JSONB DEFAULT '{}' NOT NULL,
    is_default BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Create text_messages table
CREATE TABLE text_messages (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    content TEXT NOT NULL,
    api_config_id UUID REFERENCES api_configs(id) ON DELETE CASCADE NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'sent', 'failed')) NOT NULL,
    response_status INTEGER,
    response_data JSONB,
    error_message TEXT,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL
);

-- Create indexes
CREATE INDEX idx_api_configs_user_id ON api_configs(user_id);
CREATE INDEX idx_api_configs_is_default ON api_configs(user_id, is_default) WHERE is_default = true;
CREATE INDEX idx_text_messages_user_id ON text_messages(user_id);
CREATE INDEX idx_text_messages_api_config_id ON text_messages(api_config_id);
CREATE INDEX idx_text_messages_status ON text_messages(status);
CREATE INDEX idx_text_messages_created_at ON text_messages(created_at);

-- Enable Row Level Security
ALTER TABLE api_configs ENABLE ROW LEVEL SECURITY;
ALTER TABLE text_messages ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for api_configs
CREATE POLICY "Users can view their own API configs" ON api_configs
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own API configs" ON api_configs
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own API configs" ON api_configs
    FOR UPDATE USING (auth.uid() = user_id);

CREATE POLICY "Users can delete their own API configs" ON api_configs
    FOR DELETE USING (auth.uid() = user_id);

-- Create RLS policies for text_messages
CREATE POLICY "Users can view their own text messages" ON text_messages
    FOR SELECT USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own text messages" ON text_messages
    FOR INSERT WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can update their own text messages" ON text_messages
    FOR UPDATE USING (auth.uid() = user_id);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for updated_at
CREATE TRIGGER update_api_configs_updated_at 
    BEFORE UPDATE ON api_configs 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();