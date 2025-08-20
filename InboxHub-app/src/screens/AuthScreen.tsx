import React, { useState } from 'react';
import { View, SafeAreaView, StyleSheet } from 'react-native';
import { AuthForm } from '../components/auth/AuthForm';

export function AuthScreen() {
  const [mode, setMode] = useState<'login' | 'signup'>('login');

  const handleToggleMode = () => {
    setMode(mode === 'login' ? 'signup' : 'login');
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <AuthForm mode={mode} onToggleMode={handleToggleMode} />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#ffffff',
  },
  content: {
    flex: 1,
  },
});