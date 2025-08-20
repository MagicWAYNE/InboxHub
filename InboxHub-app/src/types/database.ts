export interface Database {
  public: {
    Tables: {
      profiles: {
        Row: {
          id: string;
          email: string;
          display_name?: string;
          phone?: string;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id: string;
          email: string;
          display_name?: string;
          phone?: string;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          email?: string;
          display_name?: string;
          phone?: string;
          created_at?: string;
          updated_at?: string;
        };
      };
      api_configs: {
        Row: {
          id: string;
          user_id: string;
          name: string;
          url: string;
          method: 'GET' | 'POST' | 'PUT' | 'DELETE';
          headers: any;
          is_default: boolean;
          request_body_field: string;
          created_at: string;
          updated_at: string;
        };
        Insert: {
          id?: string;
          user_id: string;
          name: string;
          url: string;
          method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
          headers?: any;
          is_default?: boolean;
          request_body_field?: string;
          created_at?: string;
          updated_at?: string;
        };
        Update: {
          id?: string;
          user_id?: string;
          name?: string;
          url?: string;
          method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
          headers?: any;
          is_default?: boolean;
          request_body_field?: string;
          created_at?: string;
          updated_at?: string;
        };
      };
      text_messages: {
        Row: {
          id: string;
          user_id: string;
          content: string;
          api_config_id: string;
          status: 'pending' | 'sent' | 'failed';
          response_status?: number;
          response_data?: any;
          error_message?: string;
          sent_at?: string;
          created_at: string;
        };
        Insert: {
          id?: string;
          user_id: string;
          content: string;
          api_config_id: string;
          status?: 'pending' | 'sent' | 'failed';
          response_status?: number;
          response_data?: any;
          error_message?: string;
          sent_at?: string;
          created_at?: string;
        };
        Update: {
          id?: string;
          user_id?: string;
          content?: string;
          api_config_id?: string;
          status?: 'pending' | 'sent' | 'failed';
          response_status?: number;
          response_data?: any;
          error_message?: string;
          sent_at?: string;
          created_at?: string;
        };
      };
    };
    Views: {
      [_ in never]: never;
    };
    Functions: {
      [_ in never]: never;
    };
    Enums: {
      [_ in never]: never;
    };
  };
}