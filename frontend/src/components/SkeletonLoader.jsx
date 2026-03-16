export function SkeletonCard() {
  return (
    <div className="skeleton-card" style={{
      background: 'var(--bg-secondary)', borderRadius: '16px', overflow: 'hidden',
      animation: 'skeleton-pulse 1.5s ease-in-out infinite',
    }}>
      <div style={{ height: '180px', background: 'var(--bg-tertiary)' }} />
      <div style={{ padding: '1.25rem' }}>
        <div style={{ height: '20px', background: 'var(--bg-tertiary)', borderRadius: '4px', marginBottom: '0.75rem', width: '80%' }} />
        <div style={{ height: '14px', background: 'var(--bg-tertiary)', borderRadius: '4px', marginBottom: '0.5rem', width: '100%' }} />
        <div style={{ height: '14px', background: 'var(--bg-tertiary)', borderRadius: '4px', marginBottom: '1rem', width: '60%' }} />
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <div style={{ height: '14px', background: 'var(--bg-tertiary)', borderRadius: '4px', width: '40%' }} />
          <div style={{ height: '14px', background: 'var(--bg-tertiary)', borderRadius: '4px', width: '30%' }} />
        </div>
      </div>
    </div>
  )
}

export function SkeletonGrid({ count = 6 }) {
  return (
    <div className="events-scroll">
      {Array.from({ length: count }).map((_, i) => (
        <SkeletonCard key={i} />
      ))}
    </div>
  )
}
